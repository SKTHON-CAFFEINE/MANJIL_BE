package com.skthon.manjil.domain.reco.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skthon.manjil.domain.exercise.entity.Exercise;
import com.skthon.manjil.domain.exercise.repository.ExerciseRepository;
import com.skthon.manjil.domain.reco.dto.ConditionRequest;
import com.skthon.manjil.domain.reco.dto.FitnessLevel;
import com.skthon.manjil.domain.reco.dto.RecommendCard;
import com.skthon.manjil.domain.reco.dto.RecommendResponse;
import com.skthon.manjil.domain.reco.dto.RecommendResponse.DiseaseDto;
import com.skthon.manjil.domain.reco.entity.RecoSnapshot;
import com.skthon.manjil.domain.reco.repository.RecoSnapshotRepository;
import com.skthon.manjil.domain.reco.support.Fallbacks;
import com.skthon.manjil.domain.reco.support.PromptFactory;
import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.domain.user.entity.UserDisease;
import com.skthon.manjil.domain.user.repository.UserRepository;
import com.skthon.manjil.global.ai.OpenAiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

  private final OpenAiClient ai;
  private final ObjectMapper om = new ObjectMapper();
  private final ExerciseRepository exerciseRepository;
  private final UserRepository userRepository;
  private final RecoSnapshotRepository recoSnapshotRepository; // ★ 스냅샷 저장소

  private static String truncate(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
  }

  /** 로그인 사용자(userId)의 DB 프로필 + 오늘의 컨디션(condition)을 조합해서 운동을 추천 */
  public RecommendResponse recommendForUserWithCondition(Long userId, ConditionRequest condition) {
    User u =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

    // 프롬프트용: 한글 질환명 리스트
    List<String> diseasesKo =
        u.getUserDiseases().stream()
            .map(UserDisease::getDisease)
            .map(d -> d.getType())
            .filter(s -> s != null && !s.isBlank())
            .toList();

    // 응답용: 질환 DTO(id, type, caution)
    List<DiseaseDto> diseaseDtos =
        u.getUserDiseases().stream()
            .map(UserDisease::getDisease)
            .filter(d -> d != null)
            .map(
                d ->
                    new DiseaseDto(
                        d.getId(), d.getType(), d.getCaution() == null ? "" : d.getCaution()))
            .toList();

    FitnessLevel fitness = FitnessLevel.fromCode(u.getFitnessLevel());
    List<Exercise> allowed = exerciseRepository.findAllByOrderByIdAsc();

    String sys = PromptFactory.systemPromptKo();
    String user =
        PromptFactory.userPromptKoWithCondition(
            u.getAge(), u.getGender(), diseasesKo, fitness, condition, allowed);

    try {
      // 1) 호출
      String raw = ai.chatCompletionJson(sys, user);
      log.info(
          "[Reco] raw response len={} preview={}",
          (raw == null ? 0 : raw.length()),
          truncate(raw, 800));

      // 2) content 추출
      var root = om.readTree(raw);
      var choices = root.path("choices");
      String content =
          choices.isArray() && choices.size() > 0
              ? choices.get(0).path("message").path("content").asText(null)
              : null;

      log.info(
          "[Reco] content string len={} preview={}",
          (content == null ? 0 : content.length()),
          truncate(content, 800));

      if (content == null || content.isBlank()) {
        throw new IllegalStateException("assistant-content-empty");
      }

      // 3) content(JSON 문자열) 실제 파싱
      AiOut out = om.readValue(content, AiOut.class);
      log.info(
          "[Reco] parsed cards={} disclaimer?={}",
          (out == null || out.cards == null) ? 0 : out.cards.size(),
          (out != null && out.disclaimer != null && !out.disclaimer.isBlank()));

      if (out == null || out.cards == null || out.cards.isEmpty()) {
        throw new IllegalStateException("empty-cards");
      }

      // 4) 검증/보정 + 컨디션 기반 '횟수(reps)' 보정 (세트 개념 제거)
      Map<Long, Exercise> byId = new LinkedHashMap<>();
      for (Exercise e : allowed) byId.put(e.getId(), e);

      Map<String, Exercise> byName = new LinkedHashMap<>();
      for (Exercise e : allowed) byName.put(e.getName(), e);

      List<RecommendCard> cards = new ArrayList<>();
      for (AiCard c : out.cards) {
        Exercise ex = null;
        if (c.exerciseId != null) ex = byId.get(c.exerciseId);
        if (ex == null && c.name != null && !c.name.isBlank()) {
          ex = byName.get(c.name);
        }
        if (ex == null) {
          log.debug("[Reco] skip unknown exercise (id={}, name={})", c.exerciseId, c.name);
          continue;
        }

        int repsRaw = (c.value == null ? 0 : c.value);
        int repsAdj = Fallbacks.adjustRepsByCondition(Math.max(repsRaw, 1), condition);

        List<RecommendCard.ExerciseDetailDto> detailDtos = mapDetails(ex);
        String advantages = ex.getAdvantages() == null ? "" : ex.getAdvantages();

        cards.add(
            new RecommendCard(
                ex.getId(), ex.getName(), repsAdj, ex.getUnit(), detailDtos, advantages));
      }

      if (cards.size() > 4) {
        cards = new ArrayList<>(cards.subList(0, 4));
      }
      if (cards.size() < 4) {
        java.util.Set<Long> picked = new java.util.HashSet<>();
        for (RecommendCard rc : cards) picked.add(rc.exerciseId());

        for (Exercise e : allowed) {
          if (cards.size() >= 4) break;
          if (picked.contains(e.getId())) continue;

          int repsFill = Fallbacks.adjustRepsByCondition(10, condition);
          List<RecommendCard.ExerciseDetailDto> detailDtos = mapDetails(e);
          String advantages = e.getAdvantages() == null ? "" : e.getAdvantages();

          cards.add(
              new RecommendCard(
                  e.getId(), e.getName(), repsFill, e.getUnit(), detailDtos, advantages));
          picked.add(e.getId());
        }
      }

      if (cards.isEmpty()) throw new IllegalStateException("no-valid-cards");

      String disclaimer =
          (out.disclaimer == null || out.disclaimer.isBlank())
              ? "일반적 가이드이며 진단/치료가 아닙니다. 증상 악화 시 즉시 중단하고 전문가와 상담하세요."
              : out.disclaimer;

      log.info(
          "[Reco] final cards={} exampleFirst={}",
          cards.size(),
          cards.isEmpty() ? null : cards.get(0));
      return new RecommendResponse(cards, disclaimer, diseaseDtos);

    } catch (Exception e) {
      log.warn("AI recommend failed, fallback used. cause={}", e.toString());
      return Fallbacks.dynamicMinimal(
          u.getAge(), u.getGender(), fitness, condition, allowed, diseaseDtos);
    }
  }

  /** 날짜 포함: 생성하고 스냅샷 저장 */
  @Transactional
  public RecommendResponse createAndSaveForDate(
      Long userId, LocalDate date, ConditionRequest condition) {
    RecommendResponse res = recommendForUserWithCondition(userId, condition);

    String json;
    try {
      json = om.writeValueAsString(res);
    } catch (Exception e) {
      throw new IllegalStateException("recommend-snapshot-serialize-failed", e);
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

    RecoSnapshot snap =
        recoSnapshotRepository
            .findByUserAndTargetDate(user, date)
            .map(s -> s.updatePayload(json))
            .orElseGet(
                () -> RecoSnapshot.builder().user(user).targetDate(date).payloadJson(json).build());

    recoSnapshotRepository.save(snap);

    return res;
  }

  /** 날짜 포함: 저장된 스냅샷 조회 */
  @Transactional(readOnly = true)
  public RecommendResponse getSavedForDate(Long userId, LocalDate date) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

    RecoSnapshot snap =
        recoSnapshotRepository
            .findByUserAndTargetDate(user, date)
            .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 저장된 추천이 없습니다."));

    try {
      return om.readValue(snap.getPayloadJson(), RecommendResponse.class);
    } catch (Exception e) {
      throw new IllegalStateException("recommend-snapshot-deserialize-failed", e);
    }
  }

  /** Exercise → RecommendCard.ExerciseDetailDto 리스트 매핑 (null-safe) */
  private static List<RecommendCard.ExerciseDetailDto> mapDetails(Exercise ex) {
    if (ex == null || ex.getDetails() == null) return Collections.emptyList();
    return ex.getDetails().stream()
        .map(
            d ->
                new RecommendCard.ExerciseDetailDto(d.getId(), d.getImageUrl(), d.getDescription()))
        .toList();
  }

  // ==== 내부 파싱용 DTO (AI 응답 스키마) ====
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class AiOut {
    public List<AiCard> cards;
    public String disclaimer;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class AiCard {
    public Long exerciseId;
    public Integer value;
    public String name;
  }
}

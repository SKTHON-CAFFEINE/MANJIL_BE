// src/main/java/com/skthon/manjil/domain/reco/support/PromptFactory.java
package com.skthon.manjil.domain.reco.support;

import java.util.List;
import java.util.stream.Collectors;

import com.skthon.manjil.domain.exercise.entity.Exercise;
import com.skthon.manjil.domain.reco.dto.ConditionRequest;
import com.skthon.manjil.domain.reco.dto.FitnessLevel;
import com.skthon.manjil.domain.user.entity.User;

public class PromptFactory {

  public static String systemPromptKo() {
    return """
          너는 건강앱 '만질만질'의 운동 코치다.
              사용자 프로필(나이, 성별, 질환: 한글, 체력수준)과 '오늘의 컨디션(수면/피로/근육통)'을 고려해
          아래 JSON 스키마 '형태로만' 응답한다. 추가 텍스트/마크다운 금지.

          출력 스키마:
          {
            "cards":[
            {"exerciseId": number, "name": string, "sets": number, "value": number}
            ],
            "disclaimer":"string"
          }

          규칙:
              - exerciseId는 반드시 '허용된 운동 목록'에서만 선택하고, name은 카탈로그의 name을 그대로 사용.
              - cards는 '정확히 4개'만 생성할 것.
          - 중복 금지: 4개의 카드 모두 exerciseId와 name이 서로 달라야 함.
              - sets는 1~3 범위, value는 해당 운동의 단위(회/분)에 맞는 정수.
              - 안전: 고혈압(발살바 금지/저중강도), 당뇨·고지혈증(대근육/점진적),
          관절염·골다공증(저충격/가동범위 내/점진).
              - 컨디션 고려: 수면이 부족하거나 피로/근육통이 높으면 저강도·저충격·볼륨↓.
              - 반드시 JSON만 출력(설명/추가 문장/마크다운 금지).

          검증(생성 후 자체 체크):
              - cards.length === 4
              - 모든 exerciseId가 서로 다름 && 모든 name이 서로 다름
          - 각 sets ∈ {1,2,3} && value는 정수
        """;
  }

  public static String userPromptKoWithCondition(
      int age,
      User.Gender gender,
      List<String> diseasesKo,
      FitnessLevel fitnessLevel,
      ConditionRequest condition,
      List<Exercise> allowedExercises) {
    String diseaseList =
        (diseasesKo == null || diseasesKo.isEmpty()) ? "없음" : String.join(", ", diseasesKo);

    String allowed =
        allowedExercises.stream()
            .map(e -> e.getId() + ":" + e.getName() + "(" + e.getUnit() + ")")
            .collect(Collectors.joining(", "));

    return """
      사용자 프로필:
      - 나이: %d
      - 성별: %s
      - 보유 질환(한글): %s
      - 체력수준: %s

      오늘의 컨디션:
      - 수면: %s
      - 피로도: %s
      - 근육통: %s

      허용된 운동 목록(id:이름(unit)):
      %s

      요구사항:
      - cards는 3~5개.
      - 각 card는 {"exerciseId","sets","value"}만 포함(단위 텍스트 금지).
      - disclaimer에는 "일반적 가이드이며 진단/치료가 아닙니다..." 포함.
      - 반드시 JSON만 출력.
      """
        .formatted(
            age,
            gender,
            diseaseList,
            fitnessLevel,
            condition.sleep(),
            condition.fatigue(),
            condition.soreness(),
            allowed);
  }
}

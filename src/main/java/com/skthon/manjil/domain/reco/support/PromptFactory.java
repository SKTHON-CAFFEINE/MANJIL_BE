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
          {"exerciseId": number, "sets": number, "value": number}
        ],
        "disclaimer":"string"
      }

      규칙:
      - exerciseId는 반드시 '허용된 운동 목록'에서만 선택.
      - sets는 1~3 범위, value는 단위(회/분)에 맞는 정수.
      - 안전: 고혈압(발살바 금지/저중강도), 당뇨·고지혈증(대근육/점진적),
             관절염·골다공증(저충격/범위 내/점진).
      - 컨디션 고려: 수면이 부족하거나 피로/근육통이 높으면 저강도·저충격·볼륨↓.
      - 반드시 JSON만 출력.
      - 각 카드에는 exerciseId와 함께 name(카탈로그의 name 그대로)을 반드시 넣어라.
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

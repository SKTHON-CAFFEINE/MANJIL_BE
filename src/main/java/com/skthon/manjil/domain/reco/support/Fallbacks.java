// src/main/java/com/skthon/manjil/domain/reco/support/Fallbacks.java
package com.skthon.manjil.domain.reco.support;

import java.util.ArrayList;
import java.util.List;

import com.skthon.manjil.domain.exercise.entity.Exercise;
import com.skthon.manjil.domain.reco.dto.ConditionRequest;
import com.skthon.manjil.domain.reco.dto.FitnessLevel;
import com.skthon.manjil.domain.reco.dto.RecommendCard;
import com.skthon.manjil.domain.reco.dto.RecommendResponse;
import com.skthon.manjil.domain.user.entity.User;

public class Fallbacks {

  public static RecommendResponse dynamicMinimal(
      int age,
      User.Gender gender,
      FitnessLevel fitness,
      ConditionRequest condition,
      List<Exercise> allowed) {
    // 1) 저충격 위주 풀
    List<Exercise> pool = new ArrayList<>();
    for (Exercise e : allowed) {
      String n = e.getName();
      if (n == null) continue;
      if (n.contains("줄넘기") || n.contains("마운틴") || n.toLowerCase().contains("mountain")) continue;
      pool.add(e);
    }
    if (pool.isEmpty()) pool = allowed;

    // 2) 기본 볼륨
    int sets =
        switch (fitness) {
          case STRETCH -> 1;
          case LIGHT_STRENGTH -> 2;
          case REGULAR -> 2;
          case ACTIVE -> 3;
        };
    int value =
        switch (fitness) {
          case STRETCH -> 8;
          case LIGHT_STRENGTH -> 10;
          case REGULAR -> 12;
          case ACTIVE -> 15;
        };

    // 3) 컨디션에 따라 볼륨 다운
    int[] adj = adjustByCondition(sets, value, condition);
    sets = adj[0];
    value = adj[1];

    // 4) 3개 카드 구성
    List<RecommendCard> cards = new ArrayList<>();
    for (Exercise e : pool) {
      if (cards.size() >= 3) break;
      cards.add(new RecommendCard(e.getId(), e.getName(), sets, value, e.getUnit()));
    }

    return new RecommendResponse(cards, "일반적 가이드이며 진단/치료가 아닙니다. 증상 악화 시 즉시 중단하고 전문가와 상담하세요.");
  }

  /** 컨디션을 기반으로 세트/반복 수치 보정 */
  public static int[] adjustByCondition(int sets, int value, ConditionRequest c) {
    if (c == null) return new int[] {sets, value};

    // 수면/피로/근육통이 나쁠수록 강하게 다운
    int down = 0;
    switch (c.sleep()) {
      case POOR -> down += 1;
      case NORMAL -> down += 0;
      case GOOD -> down += 0;
    }
    switch (c.fatigue()) {
      case HIGH -> down += 1;
      case MEDIUM -> down += 0;
      case LOW -> down += 0;
    }
    switch (c.soreness()) {
      case HEAVY -> down += 1;
      case LIGHT -> down += 0;
      case NONE -> down += 0;
    }

    int newSets = Math.max(1, sets - down); // 최소 1세트
    int newValue = (int) Math.max(1, Math.round(value * (1.0 - 0.15 * down))); // 단계당 -15%

    return new int[] {newSets, newValue};
  }
}

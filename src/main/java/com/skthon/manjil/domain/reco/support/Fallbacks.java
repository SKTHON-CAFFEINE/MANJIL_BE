package com.skthon.manjil.domain.reco.support;

import java.util.ArrayList;
import java.util.Collections;
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

    // 1) 저충격 후보
    List<Exercise> pool = new ArrayList<>();
    for (Exercise e : allowed) {
      String n = e.getName();
      if (n == null) continue;
      if (n.contains("줄넘기") || n.contains("마운틴") || n.toLowerCase().contains("mountain")) continue;
      pool.add(e);
    }
    if (pool.isEmpty()) pool = allowed;

    // 2) 기본 '횟수(reps)' 기준치
    int baseReps =
        switch (fitness) {
          case STRETCH -> 8;
          case LIGHT_STRENGTH -> 10;
          case REGULAR -> 12;
          case ACTIVE -> 15;
        };

    // 3) 컨디션으로 '횟수'만 보정
    int reps = adjustRepsByCondition(baseReps, condition);

    // 4) 카드 구성 (세트 없음) — 예: 4개
    List<RecommendCard> cards = new ArrayList<>();
    for (Exercise e : pool) {
      if (cards.size() >= 4) break;
      // RecommendCard 생성자가 (exerciseId, name, reps, unit) 시그니처여야 합니다
      cards.add(
          new RecommendCard(e.getId(), e.getName(), reps, e.getUnit(), Collections.emptyList()));
    }

    return new RecommendResponse(cards, "일반적 가이드이며 진단/치료가 아닙니다. 증상 악화 시 즉시 중단하고 전문가와 상담하세요.");
  }

  /** 컨디션 기반 '횟수'만 보정 (세트 개념 제거) */
  public static int adjustRepsByCondition(int reps, ConditionRequest c) {
    if (c == null) return Math.max(1, reps);

    // 수면/피로/근육통이 나쁠수록 단계당 -15%
    int down = 0;
    switch (c.sleep()) {
      case POOR -> down += 1;
      default -> {}
    }
    switch (c.fatigue()) {
      case HIGH -> down += 1;
      default -> {}
    }
    switch (c.soreness()) {
      case HEAVY -> down += 1;
      default -> {}
    }

    // 단계당 -15%, 최소 1회
    return (int) Math.max(1, Math.round(reps * (1.0 - 0.15 * down)));
  }
}

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
      List<Exercise> allowed,
      List<RecommendResponse.DiseaseDto> diseases) {
    List<Exercise> pool = new ArrayList<>();
    for (Exercise e : allowed) {
      String n = e.getName();
      if (n == null) continue;
      if (n.contains("줄넘기") || n.contains("마운틴") || n.toLowerCase().contains("mountain")) continue;
      pool.add(e);
    }
    if (pool.isEmpty()) pool = allowed;

    int baseReps =
        switch (fitness) {
          case STRETCH -> 8;
          case LIGHT_STRENGTH -> 10;
          case REGULAR -> 12;
          case ACTIVE -> 15;
        };

    int reps = adjustRepsByCondition(baseReps, condition);

    List<RecommendCard> cards = new ArrayList<>();
    for (Exercise e : pool) {
      if (cards.size() >= 4) break;
      String advantages = e.getAdvantages() == null ? "" : e.getAdvantages();
      cards.add(
          new RecommendCard(e.getId(), e.getName(), reps, e.getUnit(), List.of(), advantages));
    }

    String disclaimer = "일반적 가이드이며 진단/치료가 아닙니다. 증상 악화 시 즉시 중단하고 전문가와 상담하세요.";
    return new RecommendResponse(cards, disclaimer, diseases);
  }

  public static int adjustRepsByCondition(int reps, ConditionRequest c) {
    if (c == null) return Math.max(1, reps);
    int down = 0;
    switch (c.sleep()) {
      case POOR -> down++;
      default -> {}
    }
    switch (c.fatigue()) {
      case HIGH -> down++;
      default -> {}
    }
    switch (c.soreness()) {
      case HEAVY -> down++;
      default -> {}
    }
    return Math.max(1, (int) Math.round(reps * (1.0 - 0.15 * down)));
  }
}

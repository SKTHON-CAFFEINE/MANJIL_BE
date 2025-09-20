package com.skthon.manjil.domain.reco.dto;

import java.util.List;

public record RecommendCard(
    Long exerciseId, String name, int reps, String unit, List<ExerciseDetailDto> details) {
  public record ExerciseDetailDto(Long id, String imageUrl, String description) {}
}

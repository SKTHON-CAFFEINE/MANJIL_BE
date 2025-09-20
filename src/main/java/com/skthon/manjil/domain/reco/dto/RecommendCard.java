package com.skthon.manjil.domain.reco.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추천 카드")
public record RecommendCard(
    @Schema(description = "운동 ID") Long exerciseId,
    @Schema(description = "운동명") String name,
    @Schema(description = "추천 횟수") int reps,
    @Schema(description = "단위 (회/분)") String unit,
    @Schema(description = "운동 상세 이미지/설명 목록") List<ExerciseDetailDto> details,
    @Schema(description = "운동 강점 설명") String advantages) {
  @Schema(description = "운동 상세 단계")
  public record ExerciseDetailDto(Long id, String imageUrl, String description) {}
}

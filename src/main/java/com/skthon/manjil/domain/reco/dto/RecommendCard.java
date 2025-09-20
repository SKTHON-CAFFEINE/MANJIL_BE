package com.skthon.manjil.domain.reco.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추천 운동 카드")
public record RecommendCard(
    @Schema(description = "운동 ID (DB)", example = "18") Long exerciseId,
    @Schema(description = "운동명", example = "팔굽혀펴기") String name,
    @Schema(description = "세트 수", example = "2") Integer sets,
    @Schema(description = "권장 수치 (단위는 unit)", example = "12") Integer value,
    @Schema(description = "단위", example = "회") String unit) {}

package com.skthon.manjil.domain.reco.dto;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오늘의 컨디션 입력")
public record ConditionRequest(
    @NotNull @Schema(description = "수면 상태", example = "GOOD") Sleep sleep,
    @NotNull @Schema(description = "피로도", example = "LOW") Fatigue fatigue,
    @NotNull @Schema(description = "근육통", example = "NONE") Soreness soreness) {
  public enum Sleep {
    GOOD,
    NORMAL,
    POOR
  } // 충분/보통/부족

  public enum Fatigue {
    LOW,
    MEDIUM,
    HIGH
  } // 피곤X/보통/피곤함

  public enum Soreness {
    NONE,
    LIGHT,
    HEAVY
  } // 없음/가벼움/심함
}

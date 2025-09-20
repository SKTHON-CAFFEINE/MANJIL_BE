package com.skthon.manjil.domain.reco.dto;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오늘의 컨디션 입력")
public record ConditionRequest(
    @NotNull @Schema(description = "수면 상태", example = "GOOD") @JsonProperty("sleep") Sleep sleep,
    @NotNull @Schema(description = "피로도", example = "LOW") @JsonProperty("fatigue") Fatigue fatigue,
    @NotNull @Schema(description = "근육통", example = "NONE") @JsonProperty("soreness")
        Soreness soreness) {
  // 객체/배열 모두 허용하는 단일 Creator
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static ConditionRequest create(JsonNode node) {
    if (node.isArray()) {
      // 배열 형태: ["GOOD","LOW","NONE"] -> [sleep, fatigue, soreness]
      String s0 = node.size() > 0 ? node.get(0).asText() : null;
      String s1 = node.size() > 1 ? node.get(1).asText() : null;
      String s2 = node.size() > 2 ? node.get(2).asText() : null;
      return new ConditionRequest(
          toEnum(Sleep.class, s0, Sleep.GOOD),
          toEnum(Fatigue.class, s1, Fatigue.LOW),
          toEnum(Soreness.class, s2, Soreness.NONE));
    } else {
      // 객체 형태: {"sleep":"GOOD","fatigue":"LOW","soreness":"NONE"} (키 순서 무관)
      return new ConditionRequest(
          toEnum(Sleep.class, node.path("sleep").asText(null), Sleep.GOOD),
          toEnum(Fatigue.class, node.path("fatigue").asText(null), Fatigue.LOW),
          toEnum(Soreness.class, node.path("soreness").asText(null), Soreness.NONE));
    }
  }

  private static <E extends Enum<E>> E toEnum(Class<E> type, String raw, E def) {
    if (raw == null || raw.isBlank()) return def;
    try {
      return Enum.valueOf(type, raw.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      return def; // 잘못된 값이면 기본값으로
    }
  }

  public enum Sleep {
    GOOD,
    NORMAL,
    POOR
  }

  public enum Fatigue {
    LOW,
    MEDIUM,
    HIGH
  }

  public enum Soreness {
    NONE,
    LIGHT,
    HEAVY
  }
}

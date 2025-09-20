package com.skthon.manjil.domain.reco.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecommendRequest(
    @NotNull Disease disease, @NotNull @Min(10) @Max(120) Integer age, @NotNull Level level) {}

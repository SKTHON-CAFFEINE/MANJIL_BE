package com.skthon.manjil.domain.exercise.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExerciseResponse DTO", description = "운동 관련 응답")
public class ExerciseResponse {}

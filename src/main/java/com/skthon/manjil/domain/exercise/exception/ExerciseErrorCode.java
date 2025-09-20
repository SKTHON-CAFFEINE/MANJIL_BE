package com.skthon.manjil.domain.exercise.exception;

import org.springframework.http.HttpStatus;

import com.skthon.manjil.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExerciseErrorCode implements BaseErrorCode {
  EXAMPLE_ERROR_CODE("EXERCISE_0000", "예시 에러코드로 커스터마이징이 필요합니다.", HttpStatus.BAD_REQUEST),

  DUPLICATE_EXERCISE_NAME("EXERCISE_0001", "이미 존재하는 운동 이름입니다.", HttpStatus.CONFLICT),
  IMAGE_INDEX_OUT_OF_RANGE(
      "EXERCISE_0002", "imageIndex가 images 배열 범위를 벗어났습니다.", HttpStatus.BAD_REQUEST),
  CREATE_FAILED("EXERCISE_0003", "운동 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

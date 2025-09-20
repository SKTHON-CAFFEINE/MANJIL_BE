package com.skthon.manjil.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.skthon.manjil.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  EXAMPLE_ERROR_CODE("USER_0000", "예시 에러코드로 커스터마이징이 필요합니다.", HttpStatus.BAD_REQUEST),

  DUPLICATE_EMAIL("USER_0001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
  INVALID_DISEASE_IDS("USER_0002", "존재하지 않는 질환 ID가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("USER_0005", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  INVALID_EMAIL_FORMAT("USER_0006", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD_FORMAT("USER_0007", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_EMAIL_AND_PASSWORD("USER_0008", "이메일과 비밀번호 형식이 모두 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

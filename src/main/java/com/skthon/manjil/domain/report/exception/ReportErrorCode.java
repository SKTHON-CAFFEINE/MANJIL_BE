package com.skthon.manjil.domain.report.exception;

import com.skthon.manjil.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {
  EXAMPLE_ERROR_CODE("REPORT_0000", "예시 에러코드로 커스터마이징이 필요합니다.", HttpStatus.BAD_REQUEST),

  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}

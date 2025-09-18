package com.skthon.manjil.infra.s3.exception;

import org.springframework.http.HttpStatus;

import com.skthon.manjil.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {
  FILE_NOT_FOUND("IMG4001", "존재하지 않는 이미지입니다.", HttpStatus.NOT_FOUND),
  FILE_SIZE_INVALID("IMG4002", "파일 크기는 5MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
  FILE_TYPE_INVALID("IMG4003", "이미지 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_CONTENT_TYPE("IMG4004", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
  FILE_NAME_MISSING("IMG4006", "파일 이름이 누락되었습니다.", HttpStatus.BAD_REQUEST),
  INVALID_BASE64("IMG4005", "잘못된 Base64값 입니다.", HttpStatus.BAD_REQUEST),

  FILE_SERVER_ERROR("IMG5001", "이미지 처리 중 서버 에러, 관리자에게 문의 바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  S3_CONNECTION_FAILED("IMG5002", "S3 연결 실패 또는 인증 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  IO_EXCEPTION("IMG5003", "이미지 업로드 중 입출력 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

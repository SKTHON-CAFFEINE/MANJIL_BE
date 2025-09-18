package com.skthon.manjil.global.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.skthon.manjil.global.exception.model.BaseErrorCode;
import com.skthon.manjil.global.response.BaseResponse;
import com.skthon.manjil.infra.s3.exception.S3ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 예외
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
    BaseErrorCode errorCode = ex.getErrorCode();
    log.error("Custom 오류 발생: {}", ex.getMessage());
    return ResponseEntity.status(errorCode.getStatus())
        .body(BaseResponse.error(errorCode.getStatus().value(), ex.getMessage()));
  }

  // Validation 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    String errorMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage()))
            .collect(Collectors.joining(" / "));
    log.warn("Validation 오류 발생: {}", errorMessages);
    return ResponseEntity.badRequest().body(BaseResponse.error(400, errorMessages));
  }

  /** 파일 업로드 크기 초과 예외 처리 Multipart 요청에서 파일이 5MB 이상일 경우 발생합니다. */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<BaseResponse<Object>> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex) {
    log.warn("파일 용량 초과: {}", ex.getMessage());
    return ResponseEntity.status(S3ErrorCode.FILE_SIZE_INVALID.getStatus())
        .body(
            BaseResponse.error(
                S3ErrorCode.FILE_SIZE_INVALID.getStatus().value(),
                S3ErrorCode.FILE_SIZE_INVALID.getMessage()));
  }

  /** 잘못된 파일 형식 등 이미지 업로드 관련 IllegalArgument 처리 예: Base64 디코딩 실패, Content-Type 잘못됨 등 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<BaseResponse<Object>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    log.warn("잘못된 요청 인자: {}", ex.getMessage());

    // 예외 메시지에 따라 적절한 S3 에러코드 분기 처리 (예: 파일 형식 오류)
    if (ex.getMessage().contains("파일 형식") || ex.getMessage().contains("이미지")) {
      return ResponseEntity.status(S3ErrorCode.FILE_TYPE_INVALID.getStatus())
          .body(
              BaseResponse.error(
                  S3ErrorCode.FILE_TYPE_INVALID.getStatus().value(),
                  S3ErrorCode.FILE_TYPE_INVALID.getMessage()));
    }

    return ResponseEntity.badRequest().body(BaseResponse.error(400, ex.getMessage()));
  }

  // 예상치 못한 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> handleException(Exception ex) {
    log.error("Server 오류 발생: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error(500, "예상치 못한 서버 오류가 발생했습니다."));
  }
}

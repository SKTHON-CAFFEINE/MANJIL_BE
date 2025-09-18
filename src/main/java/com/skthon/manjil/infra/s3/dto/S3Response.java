package com.skthon.manjil.infra.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "S3Response DTO", description = "이미지 업로드에 대한 응답 반환")
public class S3Response {

  @Schema(description = "이미지 이름", example = "abc123.png")
  private String fileName;

  @Schema(description = "이미지 URL", example = "https://~")
  private String imageUrl;
}

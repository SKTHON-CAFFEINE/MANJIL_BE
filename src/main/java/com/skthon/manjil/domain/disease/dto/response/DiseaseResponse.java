package com.skthon.manjil.domain.disease.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "질환 응답 DTO")
public class DiseaseResponse {

  @Schema(description = "질환 ID", example = "1")
  private Long id;

  @Schema(description = "질환 종류", example = "고혈압")
  private String type;

  @Schema(description = "운동 주의사항", example = "혈압이 높은 경우 가벼운 유산소 운동을 권장합니다.")
  private String caution;
}

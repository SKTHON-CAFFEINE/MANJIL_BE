package com.skthon.manjil.domain.disease.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DiseaseRequest DTO", description = "질환 관련 요청")
public class DiseaseRequest {

  @Schema(description = "질환 종류", example = "고혈압")
  @NotBlank(message = "질환 종류(type)는 필수입니다.")
  private String type;

  @Schema(description = "운동 주의사항", example = "혈압이 높은 경우 무거운 웨이트보다는 가벼운 유산소 운동을 권장합니다.")
  private String caution;
}

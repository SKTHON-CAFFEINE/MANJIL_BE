package com.skthon.manjil.domain.exercise.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "ExerciseRequest DTO", description = "운동 관련 요청")
public class ExerciseRequest {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "CreateRequest", description = "운동 생성 요청")
  public static class CreateRequest {

    @NotBlank(message = "운동 이름은 필수입니다.")
    @Size(max = 20, message = "운동 이름은 20자 이하여야 합니다.")
    @Schema(description = "운동 이름", example = "스쿼트", maxLength = 20)
    private String name;

    @NotBlank(message = "단위는 필수입니다.")
    @Size(max = 5, message = "단위는 5자 이하여야 합니다.")
    @Schema(description = "단위(예: 회, 초, 분, kg)", example = "회", maxLength = 5)
    private String unit;

    private String advantages;

    @NotEmpty(message = "운동 방법 상세는 최소 1개 이상이어야 합니다.")
    @Schema(description = "운동 방법 상세 리스트")
    private List<DetailCreate> details;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "DetailCreate", description = "운동 방법 단건")
  public static class DetailCreate {

    @Schema(
        description = "업로드된 images[] 배열에서 사용할 파일 인덱스 (없으면 이미지 없이 저장)",
        example = "0",
        nullable = true)
    private Integer imageIndex; // nullable

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    @Schema(description = "운동 방법 설명", example = "발은 어깨너비, 무릎은 발끝 넘어가지 않게..", nullable = true)
    private String description; // nullable
  }
}

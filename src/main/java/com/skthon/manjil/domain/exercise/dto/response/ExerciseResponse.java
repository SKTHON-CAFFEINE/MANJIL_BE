package com.skthon.manjil.domain.exercise.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExerciseResponse DTO", description = "운동 관련 응답")
public class ExerciseResponse {

  @Schema(description = "운동 ID", example = "1")
  private Long id;

  @Schema(description = "운동 이름", example = "스쿼트")
  private String name;

  @Schema(description = "단위", example = "회")
  private String unit;

  @Schema(description = "운동 유도 장점", example = "어때서 좋다!")
  private String advantages;

  @Schema(description = "운동 방법 상세 목록")
  private List<Detail> details;

  @Getter
  @Builder
  @Schema(name = "ExerciseResponse.Detail", description = "운동 방법 상세 응답")
  public static class Detail {

    @Schema(description = "상세 ID", example = "10")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://.../exercise/123.png", nullable = true)
    private String imageUrl;

    @Schema(description = "설명", example = "무릎이 안쪽으로 모이지 않게 주의", nullable = true)
    private String description;
  }
}

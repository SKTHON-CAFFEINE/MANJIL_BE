package com.skthon.manjil.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

  @Schema(description = "사용자명", example = "김향숙")
  private String username;

  @Schema(description = "포인트", example = "100")
  private Integer point;

  @Schema(description = "오늘 추천 여부", example = "true")
  private boolean recommendedToday;
}

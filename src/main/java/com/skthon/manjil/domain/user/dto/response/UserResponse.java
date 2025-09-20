package com.skthon.manjil.domain.user.dto.response;

import java.util.Set;

import com.skthon.manjil.domain.user.entity.User.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserResponse DTO", description = "사용자 관련 응답")
public class UserResponse {

  @Schema(description = "사용자 ID", example = "42")
  private Long id;

  @Schema(description = "사용자 이메일", example = "manjil@skuniv.ac.kr")
  private String email;

  @Schema(description = "사용자 이름(닉네임)", example = "manjil")
  private String username;

  @Schema(
      description = "성별",
      implementation = Gender.class,
      allowableValues = {"MALE", "FEMALE"},
      example = "MALE")
  private Gender gender;

  @Schema(description = "나이", example = "45")
  private Integer age;

  @Schema(description = "체력 수준 (1~4)", example = "2")
  private Integer fitnessLevel;

  @Schema(description = "연결된 질환 ID 목록", example = "[1, 3]")
  private Set<Long> diseaseIds;

  @Schema(description = "포인트", example = "100")
  private Integer point;
}

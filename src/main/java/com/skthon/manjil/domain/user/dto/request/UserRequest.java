package com.skthon.manjil.domain.user.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.skthon.manjil.domain.user.entity.User.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "UserRequest DTO", description = "사용자 관련 요청")
public class UserRequest {

  @Getter
  @AllArgsConstructor
  @Schema(name = "RegisterRequest", description = "회원가입 요청")
  public static class RegisterRequest {

    @NotBlank(message = "사용자 이메일 항목은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    @Schema(description = "사용자 이메일", example = "manjil@skuniv.ac.kr", maxLength = 50)
    private String email;

    @NotBlank(message = "비밀번호 항목은 필수입니다.")
    @Size(max = 20, message = "비밀번호는 20자 이하로 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상, 영문·숫자·특수문자를 포함해야 합니다.")
    @Schema(description = "비밀번호", example = "qwer1234!", maxLength = 20)
    private String password;

    @NotBlank(message = "사용자 이름 항목은 필수입니다.")
    @Size(min = 2, max = 20, message = "이름은 2~20자여야 합니다.")
    @Pattern(regexp = "^[가-힣A-Za-z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 가능합니다.")
    @Schema(description = "사용자 이름", example = "manjil", maxLength = 20)
    private String username;

    @NotNull(message = "성별 항목은 필수입니다.")
    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @NotNull(message = "나이 항목은 필수입니다.")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    @Max(value = 150, message = "나이는 150 이하이어야 합니다.")
    @Schema(description = "나이", example = "22")
    private Integer age;

    @Min(1)
    @Max(4)
    @NotNull(message = "체력 수준 항목은 필수입니다.")
    @Schema(description = "체력 수준(1~4)", example = "2")
    private Integer fitnessLevel;

    @NotNull(message = "질환 ID 목록은 필수입니다.")
    @Size(min = 1, message = "최소 1개 이상의 질환을 입력해야 합니다.")
    @Schema(description = "사용자 질환 ID 집합", example = "[1,3]")
    private Set<Long> diseaseIds;
  }

  @Getter
  @Schema(name = "LoginRequest", description = "로그인 요청")
  public static class LoginRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "사용자 이메일", example = "manjil@skuniv.ac.kr")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "사용자 비밀번호", example = "qwer1234!")
    private String password;
  }

  @Getter
  @Schema(name = "UpdateDiseaseRequest", description = "질환 수정 요청")
  public static class UpdateDiseaseRequest {

    @NotNull(message = "질환 ID 목록은 필수입니다.")
    @Size(min = 1, message = "최소 1개 이상의 질환을 입력해야 합니다.")
    @Schema(description = "새 질환 ID 집합(전체 교체)", example = "[2,5]")
    private Set<Long> diseaseIds;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(title = "SignUpValidateRequest", description = "회원가입 시 이메일/비밀번호 형식 검증 요청")
  public static class SignupValidateRequest {

    @Schema(description = "이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "Password123!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
  }
}

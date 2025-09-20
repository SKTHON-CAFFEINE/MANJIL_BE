package com.skthon.manjil.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skthon.manjil.domain.user.dto.request.UserRequest;
import com.skthon.manjil.domain.user.dto.response.UserInfoResponse;
import com.skthon.manjil.domain.user.dto.response.UserResponse;
import com.skthon.manjil.domain.user.service.UserService;
import com.skthon.manjil.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API")
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "회원가입",
      description =
          """
              새 사용자를 등록하고, 선택된 질환(diseaseIds)을 사용자와 연결합니다.

              요청 형식 (JSON)
              - email: 사용자 이메일 (최대 50자, 형식 검증)
              - password: 최소 8자, 영문/숫자/특수문자 포함, 최대 20자
              - username: 2~20자, 한글/영문/숫자만 허용
              - gender: 성별 (MALE | FEMALE)
              - age: 1~150
              - fitnessLevel: 1~4
              - diseaseIds: 질환 ID 배열 (최소 1개, 예: [1,3])

              동작
              1) 이메일 중복 검사 (중복 시 409)
              2) 비밀번호 암호화 저장
              3) diseaseIds 로 질환 존재 여부 검증 (없으면 400)
              4) 사용자-질환 연결 후 생성된 사용자 정보 반환(연결된 질환 ID 포함)
              """)
  @PostMapping(
      path = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<UserResponse>> registerUser(
      @Valid @RequestBody UserRequest.RegisterRequest request) {

    UserResponse userResponse = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("회원 가입이 완료되었습니다.", userResponse));
  }

  @Operation(
      summary = "내 질환 목록 수정",
      description =
          """
              현재 로그인한 사용자의 질환 목록을 요청 본문에 전달한 diseaseIds로 **전량 교체**합니다.

              동작
              1) 로그인 사용자 조회 (없으면 404)
              2) diseaseIds에 해당하는 질환 존재 여부 검증 (누락 시 400)
              3) 기존 userDiseases 모두 제거 후, 새 질환들로 매핑 생성
              4) 수정된 사용자 정보를 반환
              """)
  @PutMapping("/diseases")
  public ResponseEntity<BaseResponse<UserResponse>> updateMyDiseases(
      @Valid @RequestBody UserRequest.UpdateDiseaseRequest request) {
    UserResponse result = userService.updateCurrentUserDiseases(request);
    return ResponseEntity.ok(BaseResponse.success("질환이 수정되었습니다.", result));
  }

  @Operation(
      summary = "회원가입 정보 검증",
      description =
          """
              이메일과 비밀번호 형식을 검증합니다.
              - 200 OK: 검증 통과
              - USER_0006: 이메일 형식 오류
              - USER_0007: 비밀번호 형식 오류
              - USER_0008: 이메일 & 비밀번호 모두 오류
              """)
  @PostMapping(
      path = "/valid",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<Void>> validateSignup(
      @Valid @RequestBody UserRequest.SignupValidateRequest request) {

    userService.validateSignup(request);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  @Operation(
      summary = "내 정보 요약",
      description =
          """
              현재 로그인한 사용자의 요약 정보를 반환합니다.
              - 반환 항목:
                • 이름(username), 포인트(point), 오늘 추천 여부(recommendedToday)

              - recommendedToday 판별:
                사용자와 연결된 최신 Report 엔티티의 date 값이 오늘(LocalDate.now, Asia/Seoul 기준)과 같으면 true,
                아니면 false로 반환합니다.
              """)
  @GetMapping("/summary")
  public ResponseEntity<BaseResponse<UserInfoResponse>> getMyInfoSummary() {
    UserInfoResponse data = userService.getMyInfoSummary();
    return ResponseEntity.ok(BaseResponse.success("내 정보 요약 조회 성공입니다.", data));
  }
}

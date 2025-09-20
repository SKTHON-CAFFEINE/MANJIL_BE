package com.skthon.manjil.domain.reco.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.skthon.manjil.domain.reco.dto.ConditionRequest;
import com.skthon.manjil.domain.reco.dto.RecommendResponse;
import com.skthon.manjil.domain.reco.service.RecommendService;
import com.skthon.manjil.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reco")
@Tag(
    name = "운동 추천(만질만질)",
    description = "회원가입 시 저장된 사용자 프로필(나이/성별/질환/체력) + 오늘의 컨디션을 바탕으로 AI가 운동을 추천합니다.")
public class RecommendController {

  private final RecommendService service;

  @PostMapping("/bodyweight")
  @Operation(
      summary = "프로필+컨디션 기반 맨몸운동 추천",
      description = "로그인 사용자 프로필(DB)과 오늘의 컨디션(RequestBody)을 합쳐 AI가 운동을 추천합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "추천 생성 성공",
        content = @Content(schema = @Schema(implementation = RecommendResponse.class))),
    @ApiResponse(responseCode = "401", description = "인증 필요"),
    @ApiResponse(responseCode = "500", description = "AI 실패 시 폴백 적용")
  })
  public BaseResponse<RecommendResponse> bodyweight(
      @AuthenticationPrincipal com.skthon.manjil.global.security.CustomUserDetails me,
      @RequestBody @Valid ConditionRequest condition) {
    return BaseResponse.success(
        "추천 생성 성공", service.recommendForUserWithCondition(me.getUserId(), condition));
  }

  @GetMapping("/ping")
  @Operation(summary = "서버 상태 확인", description = "운영/모니터링용 엔드포인트")
  public String ping() {
    return "ok";
  }
}

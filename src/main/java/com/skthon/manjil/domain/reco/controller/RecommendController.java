package com.skthon.manjil.domain.reco.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

  private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

  private final RecommendService service;

  // === 오늘 추천 생성 + 저장 ===
  @PostMapping("/bodyweight")
  @Operation(
      summary = "오늘 추천 생성 + 저장",
      description = "로그인 사용자 프로필(DB)과 오늘의 컨디션(RequestBody)을 합쳐 AI가 운동을 추천하고, 오늘 날짜 스냅샷으로 저장합니다.")
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
    LocalDate today = LocalDate.now();
    return BaseResponse.success(
        "오늘 추천 생성 및 저장 성공", service.createAndSaveForDate(me.getUserId(), today, condition));
  }

  // === 특정 날짜에 저장된 추천 조회 ===
  @GetMapping("/bodyweight/{date}")
  @Operation(
      summary = "특정 날짜 저장된 추천 조회",
      description = "yyyy-MM-dd 형식의 날짜를 path로 받아 저장된 추천을 반환합니다.")
  public BaseResponse<RecommendResponse> getSavedForDate(
      @AuthenticationPrincipal com.skthon.manjil.global.security.CustomUserDetails me,
      @PathVariable("date") String dateStr) {
    LocalDate date = LocalDate.parse(dateStr, DF);
    return BaseResponse.success("저장된 추천 조회 성공", service.getSavedForDate(me.getUserId(), date));
  }

  // === 서버 상태 확인 ===
  @GetMapping("/ping")
  @Operation(summary = "서버 상태 확인", description = "운영/모니터링용 엔드포인트")
  public String ping() {
    return "ok";
  }
}

package com.skthon.manjil.domain.reco.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "운동 추천 응답")
public record RecommendResponse(
    @Schema(description = "추천 운동 카드 리스트") List<RecommendCard> cards,
    @Schema(description = "안내 문구") String disclaimer) {}

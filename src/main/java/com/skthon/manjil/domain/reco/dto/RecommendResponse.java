package com.skthon.manjil.domain.reco.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "운동 추천 응답")
public record RecommendResponse(
    @Schema(description = "추천 운동 카드 리스트") List<RecommendCard> cards,
    @Schema(description = "안내 문구") String disclaimer,
    @Schema(description = "사용자 등록 질환 목록") List<DiseaseDto> diseases) {
  @Schema(description = "질환 정보")
  public record DiseaseDto(
      @Schema(description = "질환 ID", example = "1") Long id,
      @Schema(description = "질환명", example = "고혈압") String type,
      @Schema(
              description = "운동 주의사항",
              example = "운동 중 숨을 참지 말고, 자연스럽게 호흡하세요. 더운 날씨처럼 체온이 급격히 오르는 환경은 피하세요.")
          String caution) {}
}

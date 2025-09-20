package com.skthon.manjil.domain.disease.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skthon.manjil.domain.disease.dto.request.DiseaseRequest;
import com.skthon.manjil.domain.disease.dto.response.DiseaseResponse;
import com.skthon.manjil.domain.disease.service.DiseaseService;
import com.skthon.manjil.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diseases")
@Tag(name = "Disease", description = "Disease 관련 API")
public class DiseaseController {

  private final DiseaseService diseaseService;

  @Operation(
      summary = "질환 생성",
      description =
          """
              새로운 질환을 등록합니다.
              - type: 질환 이름 (예: 고혈압, 당뇨병 등)
              - caution: 운동 시 주의사항 (선택 입력)
              """)
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<DiseaseResponse>> createDisease(
      @Valid @RequestBody DiseaseRequest request) {

    DiseaseResponse response = diseaseService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("질환이 생성되었습니다.", response));
  }
}

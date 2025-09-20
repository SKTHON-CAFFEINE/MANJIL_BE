package com.skthon.manjil.domain.exercise.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skthon.manjil.domain.exercise.dto.request.ExerciseRequest;
import com.skthon.manjil.domain.exercise.dto.response.ExerciseResponse;
import com.skthon.manjil.domain.exercise.service.ExerciseService;
import com.skthon.manjil.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
@Tag(name = "Exercise", description = "Exercise 관련 API")
public class ExerciseController {

  private final ExerciseService exerciseService;

  @Operation(
      summary = "운동 생성",
      description =
          """
              새로운 운동을 등록합니다.

              요청 형식 (multipart/form-data)
              - **exerciseRequest** (JSON):
                  - name: 운동명(최대 20자)
                  - unit: 단위(최대 5자, 예: 회/초/분/kg)
                  - details: 운동 방법 리스트
                    - details[].imageIndex: images 배열의 인덱스(선택)
                    - details[].description: 설명(선택)
              - **images** (File[]): 운동 방법 이미지들(선택, 배열)

              매핑 규칙
              - 'details[i].imageIndex = n' 이면, 'images[n]' 파일이 업로드되어 해당 상세의 imageUrl에 저장됩니다.
              - 'imageIndex'가 없거나 잘못된 인덱스면 이미지 없이 저장됩니다.

              응답
              - 생성된 운동의 기본정보 및 상세 목록을 반환합니다.
              """)
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ExerciseResponse>> createExercise(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @Valid
          @RequestPart("exerciseRequest")
          ExerciseRequest.CreateRequest exerciseRequest,
      @Parameter(
              description = "운동 방법 이미지 파일 배열 (details[].imageIndex로 참조)",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "images", required = false)
          List<MultipartFile> images) {

    ExerciseResponse response = exerciseService.create(exerciseRequest, images);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("운동이 생성되었습니다.", response));
  }
}

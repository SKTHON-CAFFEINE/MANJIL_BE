package com.skthon.manjil.infra.s3.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skthon.manjil.global.response.BaseResponse;
import com.skthon.manjil.infra.s3.dto.S3Response;
import com.skthon.manjil.infra.s3.entity.PathName;
import com.skthon.manjil.infra.s3.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3/images")
@Tag(name = "S3", description = "S3 이미지 파일 관리 API")
public class S3Controller {

  private final S3Service s3Service;

  @Operation(
      summary = "이미지 업로드",
      description =
          """
              Multipart 형식의 이미지를 S3에 업로드합니다.

              - **업로드할 위치는 pathName**으로 지정합니다.
              - 응답에는 S3에 저장된 이미지 URL과 파일명이 포함됩니다.
              - 파일은 이미지 형식만 허용되며, 5MB 이하만 업로드 가능합니다.
              """)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<S3Response>> uploadImage(
      @RequestParam PathName pathName, @RequestParam MultipartFile file) {

    S3Response s3Response = s3Service.uploadImage(pathName, file);
    return ResponseEntity.ok(BaseResponse.success("이미지 업로드에 성공했습니다.", s3Response));
  }

  @Operation(
      summary = "경로별 이미지 목록 조회",
      description =
          """
              지정된 **pathName 경로 하위**에 존재하는 모든 이미지 URL을 조회합니다.

              - S3 내부에 저장된 전체 이미지 URL 목록을 반환합니다.
              - 삭제나 갤러리 구현 시 유용하게 활용할 수 있습니다.
              """)
  @GetMapping
  public ResponseEntity<BaseResponse<List<S3Response>>> listFiles(@RequestParam PathName pathName) {
    List<S3Response> files = s3Service.getAllFiles(pathName);
    return ResponseEntity.ok(BaseResponse.success("파일 목록 조회에 성공했습니다.", files));
  }

  @Operation(
      summary = "이미지 URL 기반 이미지 삭제",
      description =
          """
              S3에 저장된 이미지의 URL을 기반으로 파일을 삭제합니다.

              - **https://{bucket}.s3.{region}.amazonaws.com/{path}/{filename}** 형식의 전체 URL이 필요합니다.
              - URL에서 keyName을 추출하여 삭제합니다.
              """)
  @DeleteMapping
  public ResponseEntity<BaseResponse<String>> deleteByUrl(@RequestParam String url) {
    s3Service.deleteByUrl(url);
    return ResponseEntity.ok(BaseResponse.success("파일 삭제에 성공했습니다."));
  }

  @Operation(
      summary = "파일명 기반 이미지 삭제",
      description =
          """
              파일명을 기준으로 S3에 저장된 이미지를 삭제합니다.

              - **삭제할 위치는 pathName, 삭제 대상은 **fileName**으로 지정합니다.
              - 실제 삭제 대상은 **{pathName}/{fileName}** 경로에 해당하는 파일입니다.
              """)
  @DeleteMapping("/{pathName}/{fileName}")
  public ResponseEntity<BaseResponse<String>> deleteFile(
      @PathVariable PathName pathName, @PathVariable String fileName) {
    s3Service.deleteFile(pathName, fileName);
    return ResponseEntity.ok(BaseResponse.success("파일 삭제에 성공했습니다."));
  }
}

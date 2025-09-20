package com.skthon.manjil.infra.s3.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.skthon.manjil.global.exception.CustomException;
import com.skthon.manjil.infra.s3.S3Config;
import com.skthon.manjil.infra.s3.dto.S3Response;
import com.skthon.manjil.infra.s3.entity.PathName;
import com.skthon.manjil.infra.s3.exception.S3ErrorCode;
import com.skthon.manjil.infra.s3.mapper.S3Mapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;
  private final S3Mapper s3Mapper;

  /** MultipartFile을 S3에 업로드하고 S3Response 객체로 반환합니다. */
  public S3Response uploadImage(PathName pathName, MultipartFile file) {
    String keyName = uploadFile(pathName, file);
    return s3Mapper.toResponse(keyName);
  }

  /** MultipartFile을 지정한 PathName에 따라 S3에 업로드하고 keyName을 반환합니다. */
  public String uploadFile(PathName pathName, MultipartFile file) {
    validateFile(file);

    String ext = getFileExtension(file.getOriginalFilename());
    String keyName = getPrefix(pathName) + "/" + UUID.randomUUID() + ext;

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
      return keyName;
    } catch (AmazonS3Exception e) {
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    } catch (IOException e) {
      throw new CustomException(S3ErrorCode.IO_EXCEPTION);
    }
  }

  /** S3에 저장된 파일의 전체 URL을 반환합니다. */
  public String getFileUrl(String keyName) {
    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }
    try {
      return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    } catch (AmazonS3Exception e) {
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    }
  }

  /** S3에서 특정 파일을 삭제합니다. */
  public void deleteFile(String keyName) {
    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }

    try {
      amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), keyName));
    } catch (AmazonS3Exception e) {
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    }
  }

  /** 지정된 PathName 경로의 모든 파일 목록을 조회합니다. */
  public List<S3Response> getAllFiles(PathName pathName) {
    String prefix = getPrefix(pathName);

    try {
      ListObjectsV2Result result =
          amazonS3.listObjectsV2(
              new ListObjectsV2Request().withBucketName(s3Config.getBucket()).withPrefix(prefix));
      return s3Mapper.toResponseList(result.getObjectSummaries());
    } catch (AmazonS3Exception e) {
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    }
  }

  /** 지정된 PathName, 파일 이름으로 파일을 삭제합니다. */
  public void deleteFile(PathName pathName, String fileName) {
    String keyName = getPrefix(pathName) + "/" + fileName;
    deleteFile(keyName);
  }

  /** 이미지 URL에서 keyName(path + fileName) 추출하여 파일을 삭제합니다. */
  public void deleteByUrl(String url) {
    try {
      String bucketUrlPrefix =
          "https://" + s3Config.getBucket() + ".s3." + s3Config.getRegion() + ".amazonaws.com/";
      if (!url.startsWith(bucketUrlPrefix)) {
        throw new CustomException(S3ErrorCode.FILE_NAME_MISSING);
      }
      String keyName = url.substring(bucketUrlPrefix.length());
      deleteFile(keyName);
    } catch (Exception e) {
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  private void validateFile(MultipartFile file) {
    if (file.getSize() > 5 * 1024 * 1024) {
      throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
  }

  private boolean validateBase64(String base64Data) {
    if (base64Data == null || base64Data.trim().isEmpty()) {
      return false;
    }
    try {
      Base64.getDecoder().decode(base64Data.contains(",") ? base64Data.split(",")[1] : base64Data);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private String getPrefix(PathName pathName) {
    return switch (pathName) {
      case USER -> s3Config.getUserFolder();
      case EXERCISE -> s3Config.getExerciseFolder();
    };
  }

  /* 확장자 추출 */
  private String getFileExtension(String originalName) {
    if (originalName == null || !originalName.contains(".")) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
    return originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
  }
}

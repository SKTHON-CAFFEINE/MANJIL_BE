package com.skthon.manjil.infra.s3.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.skthon.manjil.infra.s3.S3Config;
import com.skthon.manjil.infra.s3.dto.S3Response;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Mapper {

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  public S3Response toResponse(String keyName) {
    String url = amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    String fileName =
        keyName.contains("/") ? keyName.substring(keyName.lastIndexOf("/") + 1) : keyName;
    return S3Response.builder().fileName(fileName).imageUrl(url).build();
  }

  public List<S3Response> toResponseList(List<S3ObjectSummary> summaries) {
    return summaries.stream().map(obj -> toResponse(obj.getKey())).collect(Collectors.toList());
  }
}

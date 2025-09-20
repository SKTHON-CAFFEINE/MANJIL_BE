package com.skthon.manjil.domain.exercise.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skthon.manjil.domain.exercise.dto.request.ExerciseRequest;
import com.skthon.manjil.domain.exercise.dto.response.ExerciseResponse;
import com.skthon.manjil.domain.exercise.entity.Exercise;
import com.skthon.manjil.domain.exercise.entity.ExerciseDetail;
import com.skthon.manjil.domain.exercise.exception.ExerciseErrorCode;
import com.skthon.manjil.domain.exercise.mapper.ExerciseMapper;
import com.skthon.manjil.domain.exercise.repository.ExerciseDetailRepository;
import com.skthon.manjil.domain.exercise.repository.ExerciseRepository;
import com.skthon.manjil.global.exception.CustomException;
import com.skthon.manjil.infra.s3.entity.PathName;
import com.skthon.manjil.infra.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;
  private final ExerciseDetailRepository exerciseDetailRepository;
  private final ExerciseMapper exerciseMapper;
  private final S3Service s3Service;

  public ExerciseResponse create(ExerciseRequest.CreateRequest req, List<MultipartFile> images) {
    // 이름 중복 방지
    if (exerciseRepository.existsByName(req.getName())) {
      throw new CustomException(ExerciseErrorCode.DUPLICATE_EXERCISE_NAME);
    }

    try {
      // 1) 기본 엔티티 저장
      Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(req));

      // 2) 상세 저장 (이미지 업로드 → URL)
      for (int i = 0; i < req.getDetails().size(); i++) {
        ExerciseRequest.DetailCreate dc = req.getDetails().get(i);

        String imageUrl = null;
        if (dc.getImageIndex() != null) {
          if (images == null) {
            throw new CustomException(ExerciseErrorCode.IMAGE_INDEX_OUT_OF_RANGE);
          }
          int idx = dc.getImageIndex();
          if (idx < 0 || idx >= images.size()) {
            throw new CustomException(ExerciseErrorCode.IMAGE_INDEX_OUT_OF_RANGE);
          }
          MultipartFile file = images.get(idx);
          if (file != null && !file.isEmpty()) {
            imageUrl = s3Service.uploadImage(PathName.EXERCISE, file).getImageUrl();
          }
        }

        ExerciseDetail detail =
            exerciseMapper.toDetailEntity(exercise, imageUrl, dc.getDescription());
        exercise.addDetail(detail); // 컬렉션에 추가
      }

      // 3) 자식 저장 (cascade=ALL이라도 명시 저장 원하면 아래 라인 유지)
      exercise.getDetails().forEach(exerciseDetailRepository::save);

      // 4) 응답
      return exerciseMapper.toResponse(exercise);

    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("운동 생성 실패. req={}", req, e);
      throw new CustomException(ExerciseErrorCode.CREATE_FAILED);
    }
  }
}

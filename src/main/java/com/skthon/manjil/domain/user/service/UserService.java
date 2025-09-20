package com.skthon.manjil.domain.user.service;

import java.util.HashSet;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skthon.manjil.domain.disease.entity.Disease;
import com.skthon.manjil.domain.disease.repository.DiseaseRepository;
import com.skthon.manjil.domain.report.entity.Report;
import com.skthon.manjil.domain.report.repository.ReportRepository;
import com.skthon.manjil.domain.user.dto.request.UserRequest;
import com.skthon.manjil.domain.user.dto.request.UserRequest.UpdateDiseaseRequest;
import com.skthon.manjil.domain.user.dto.response.UserInfoResponse;
import com.skthon.manjil.domain.user.dto.response.UserResponse;
import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.domain.user.exception.UserErrorCode;
import com.skthon.manjil.domain.user.mapper.UserMapper;
import com.skthon.manjil.domain.user.repository.UserRepository;
import com.skthon.manjil.global.exception.CustomException;
import com.skthon.manjil.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final DiseaseRepository diseaseRepository;
  private final ReportRepository reportRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserResponse register(UserRequest.RegisterRequest req) {
    // 이메일 중복 체크
    if (userRepository.existsByEmail(req.getEmail())) {
      throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
    }

    // 비밀번호 암호화
    String encoded = passwordEncoder.encode(req.getPassword());

    // 유저 엔티티 생성 & 기본 저장(질환 제외)
    User user = userMapper.toEntity(req, encoded, 0);
    user = userRepository.save(user);

    // 질환 로드 및 연결
    Set<Long> ids = req.getDiseaseIds();
    if (ids != null && !ids.isEmpty()) {
      Set<Disease> diseases = new HashSet<>(diseaseRepository.findAllById(ids));
      if (diseases.size() != ids.size()) {
        throw new CustomException(UserErrorCode.INVALID_DISEASE_IDS);
      }
      user.replaceDiseases(diseases);
    }

    // 응답 변환
    return userMapper.toResponse(user);
  }

  public UserResponse updateCurrentUserDiseases(UpdateDiseaseRequest req) {
    // 1) 현재 로그인 사용자 ID 획득
    Long userId = SecurityUtil.getCurrentUserId(); // 프로젝트의 SecurityUtil에 맞춰 사용
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 2) 질환 존재 여부 검증
    Set<Long> ids = req.getDiseaseIds();
    Set<Disease> diseases = new HashSet<>(diseaseRepository.findAllById(ids));
    if (diseases.size() != ids.size()) {
      // 요청한 ID 중 실제 없는 것이 있음
      throw new CustomException(UserErrorCode.INVALID_DISEASE_IDS);
    }

    // 3) 교체
    user.replaceDiseases(diseases);

    // 4) 응답 DTO 변환
    return userMapper.toResponse(user);
  }

  public void validateSignup(UserRequest.SignupValidateRequest req) {
    boolean emailValid =
        req.getEmail() != null && req.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    boolean passwordValid =
        req.getPassword() != null
            && req.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$");

    if (!emailValid && !passwordValid) {
      throw new CustomException(UserErrorCode.INVALID_EMAIL_AND_PASSWORD);
    }
    if (!emailValid) {
      throw new CustomException(UserErrorCode.INVALID_EMAIL_FORMAT);
    }
    if (!passwordValid) {
      throw new CustomException(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }
  }

  public UserInfoResponse getMyInfoSummary() {
    Long userId = SecurityUtil.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    boolean recommendedToday = hasRecommendedToday(user.getId());

    return UserInfoResponse.builder()
        .username(user.getUsername())
        .point(user.getPoint())
        .recommendedToday(recommendedToday)
        .build();
  }

  public boolean hasRecommendedToday(Long userId) {
    // 최근 report 하나 조회 (예: 날짜 기준 내림차순)
    Report report = reportRepository.findTopByUserIdOrderByDateDesc(userId).orElse(null);

    return report != null && report.isToday();
  }
}

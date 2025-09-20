package com.skthon.manjil.domain.user.service;

import java.util.HashSet;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skthon.manjil.domain.disease.entity.Disease;
import com.skthon.manjil.domain.disease.repository.DiseaseRepository;
import com.skthon.manjil.domain.user.dto.request.UserRequest;
import com.skthon.manjil.domain.user.dto.response.UserResponse;
import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.domain.user.exception.UserErrorCode;
import com.skthon.manjil.domain.user.mapper.UserMapper;
import com.skthon.manjil.domain.user.repository.UserRepository;
import com.skthon.manjil.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final DiseaseRepository diseaseRepository;
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
    User user = userMapper.toEntity(req, encoded);
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
}

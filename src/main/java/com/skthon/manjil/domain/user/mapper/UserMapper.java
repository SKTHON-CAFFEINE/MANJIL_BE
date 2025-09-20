package com.skthon.manjil.domain.user.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skthon.manjil.domain.user.dto.request.UserRequest;
import com.skthon.manjil.domain.user.dto.response.UserResponse;
import com.skthon.manjil.domain.user.entity.User;

@Component
public class UserMapper {

  public User toEntity(UserRequest.RegisterRequest req, String encodedPassword) {
    return User.builder()
        .email(req.getEmail())
        .password(encodedPassword)
        .username(req.getUsername())
        .gender(req.getGender())
        .age(req.getAge())
        .fitnessLevel(req.getFitnessLevel())
        .build();
  }

  public UserResponse toResponse(User user) {
    // userDiseases는 User.replaceDiseases로 채워짐
    var diseaseIds =
        user.getUserDiseases().stream()
            .map(ud -> ud.getDisease().getId())
            .collect(Collectors.toSet());

    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .gender(user.getGender())
        .age(user.getAge())
        .fitnessLevel(user.getFitnessLevel())
        .diseaseIds(diseaseIds)
        .build();
  }
}

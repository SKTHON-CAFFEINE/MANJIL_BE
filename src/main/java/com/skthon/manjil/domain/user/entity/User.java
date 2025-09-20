package com.skthon.manjil.domain.user.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skthon.manjil.domain.disease.entity.Disease;
import com.skthon.manjil.global.common.BaseTimeEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "username", nullable = false)
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @Column(name = "age", nullable = false)
  private Integer age;

  @Min(1)
  @Max(4)
  @NotNull
  @Column(name = "fitness_level", nullable = false)
  private Integer fitnessLevel;

  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserDisease> userDiseases = new HashSet<>();

  @Column(name = "point", nullable = false)
  @Builder.Default
  private Integer point = 0;

  public void replaceDiseases(Set<Disease> newDiseases) {
    // 현재 보유 set의 diseaseId 집합
    Set<Long> currentIds =
        userDiseases.stream().map(ud -> ud.getDisease().getId()).collect(Collectors.toSet());

    Set<Long> newIds = newDiseases.stream().map(Disease::getId).collect(Collectors.toSet());

    // 1) 제거: 현재에만 있는 것
    userDiseases.removeIf(ud -> !newIds.contains(ud.getDisease().getId()));
    // orphanRemoval=true 이면 flush 시점에 delete

    // 2) 추가: 신규에만 있는 것
    Set<Long> toAdd = new HashSet<>(newIds);
    toAdd.removeAll(currentIds);

    for (Disease d : newDiseases) {
      if (toAdd.contains(d.getId())) {
        userDiseases.add(UserDisease.builder().user(this).disease(d).build());
      }
    }
  }

  public void addPoint(int amount) {
    this.point += amount;
  }

  public enum Gender {
    @Schema(description = "남성")
    MALE,
    @Schema(description = "여성")
    FEMALE
  }
}

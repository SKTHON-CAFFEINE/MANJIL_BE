package com.skthon.manjil.domain.user.entity;

import java.util.HashSet;
import java.util.Set;

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
    userDiseases.clear();
    for (Disease d : newDiseases) {
      userDiseases.add(UserDisease.builder().user(this).disease(d).build());
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

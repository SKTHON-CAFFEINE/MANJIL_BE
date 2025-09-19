package com.skthon.manjil.domain.disease.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "disease", uniqueConstraints = @UniqueConstraint(columnNames = "type"))
public class Disease {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "질환 ID", example = "1")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, unique = true, length = 50)
  @Schema(description = "질환 종류", implementation = DiseaseType.class)
  private DiseaseType type;

  public enum DiseaseType {
    @Schema(description = "고혈압")
    HYPERTENSION,

    @Schema(description = "당뇨")
    DIABETES,

    @Schema(description = "고지혈증")
    HYPERLIPIDEMIA,

    @Schema(description = "관절염")
    ARTHRITIS,

    @Schema(description = "골다공증")
    OSTEOPOROSIS
  }
}

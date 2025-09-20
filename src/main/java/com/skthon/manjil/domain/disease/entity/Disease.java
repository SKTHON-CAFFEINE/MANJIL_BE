package com.skthon.manjil.domain.disease.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @Column(name = "type", nullable = false, unique = true, length = 50)
  @Schema(description = "질환 종류", example = "고혈압")
  private String type;
}

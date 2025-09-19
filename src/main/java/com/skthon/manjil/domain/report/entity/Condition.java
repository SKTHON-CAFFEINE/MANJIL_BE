package com.skthon.manjil.domain.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "condition")
public class Condition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 피로도: 좋음=0, 보통=1, 나쁨=2
  @Column(name = "fatigue", nullable = false)
  private Integer fatigue;

  @Column(name = "pain", nullable = true)
  private String pain;
}

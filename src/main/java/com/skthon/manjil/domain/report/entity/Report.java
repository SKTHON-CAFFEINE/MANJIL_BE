package com.skthon.manjil.domain.report.entity;

import java.time.LocalDate;
import java.time.ZoneId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.global.common.BaseTimeEntity;

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
@Table(name = "report")
public class Report extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
  @JoinColumn(name = "health_condition_id", unique = true, nullable = true)
  private Condition condition;

  public boolean isToday() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul")); // KST 기준
    return date != null && date.isEqual(today);
  }
}

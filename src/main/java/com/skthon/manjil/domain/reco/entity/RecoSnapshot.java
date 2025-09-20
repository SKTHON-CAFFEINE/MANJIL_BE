package com.skthon.manjil.domain.reco.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.global.common.BaseTimeEntity;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(
    name = "reco_snapshot",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_date"}))
public class RecoSnapshot extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "target_date", nullable = false)
  private LocalDate targetDate;

  /** RecommendResponse 전체 JSON을 그대로 저장 (cards의 details/advantages/disclaimer까지 그대로 보관) */
  @Lob
  @Column(name = "payload_json", nullable = false, columnDefinition = "LONGTEXT")
  private String payloadJson;

  public RecoSnapshot updatePayload(String json) {
    return this.toBuilder().payloadJson(json).build();
  }
}

package com.skthon.manjil.domain.reco.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skthon.manjil.domain.reco.entity.RecoSnapshot;
import com.skthon.manjil.domain.user.entity.User;

public interface RecoSnapshotRepository extends JpaRepository<RecoSnapshot, Long> {
  Optional<RecoSnapshot> findByUserAndTargetDate(User user, LocalDate date);
}

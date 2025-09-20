package com.skthon.manjil.domain.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  Optional<Report> findTopByUserIdOrderByDateDesc(Long userId);
}

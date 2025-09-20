package com.skthon.manjil.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.report.entity.Condition;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {}

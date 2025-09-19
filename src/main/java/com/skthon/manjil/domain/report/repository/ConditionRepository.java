package com.skthon.manjil.domain.report.repository;

import com.skthon.manjil.domain.report.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {

}

package com.skthon.manjil.domain.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.exercise.entity.ExerciseDetail;

@Repository
public interface ExerciseDetailRepository extends JpaRepository<ExerciseDetail, Long> {}

package com.skthon.manjil.domain.exercise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.exercise.entity.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  List<Exercise> findAllByOrderByIdAsc();

  Optional<Exercise> findByName(String name);

  boolean existsByName(String name);

  // (필요시) 대소문자 무시 버전이 필요하다면:
  // boolean existsByNameIgnoreCase(String name);
}

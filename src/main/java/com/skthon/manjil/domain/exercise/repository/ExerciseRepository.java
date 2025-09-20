package com.skthon.manjil.domain.exercise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.exercise.entity.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  boolean existsByName(String name);

  Optional<Exercise> findByName(String name);
}

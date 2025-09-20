package com.skthon.manjil.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.user.entity.UserDisease;

@Repository
public interface UserDiseaseRepository extends JpaRepository<UserDisease, Long> {}

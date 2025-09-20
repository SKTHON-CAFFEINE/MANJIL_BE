package com.skthon.manjil.domain.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skthon.manjil.domain.recommend.entity.Recommend;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {}

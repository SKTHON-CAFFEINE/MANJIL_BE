package com.skthon.manjil.domain.recommend.repository;

import com.skthon.manjil.domain.recommend.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {

}

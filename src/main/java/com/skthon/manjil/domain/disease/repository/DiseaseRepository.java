package com.skthon.manjil.domain.disease.repository;

import com.skthon.manjil.domain.disease.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

}

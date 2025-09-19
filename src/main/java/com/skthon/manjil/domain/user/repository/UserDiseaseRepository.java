package com.skthon.manjil.domain.user.repository;

import com.skthon.manjil.domain.user.entity.UserDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDiseaseRepository extends JpaRepository<UserDisease, Long> {

}

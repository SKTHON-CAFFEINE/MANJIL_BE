package com.skthon.manjil.domain.disease.mapper;

import org.springframework.stereotype.Component;

import com.skthon.manjil.domain.disease.dto.request.DiseaseRequest;
import com.skthon.manjil.domain.disease.dto.response.DiseaseResponse;
import com.skthon.manjil.domain.disease.entity.Disease;

@Component
public class DiseaseMapper {

  public Disease toEntity(DiseaseRequest req) {
    return Disease.builder().type(req.getType()).caution(req.getCaution()).build();
  }

  public DiseaseResponse toResponse(Disease disease) {
    return DiseaseResponse.builder()
        .id(disease.getId())
        .type(disease.getType())
        .caution(disease.getCaution())
        .build();
  }
}

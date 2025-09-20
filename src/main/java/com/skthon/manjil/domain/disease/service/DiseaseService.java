package com.skthon.manjil.domain.disease.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.skthon.manjil.domain.disease.dto.request.DiseaseRequest;
import com.skthon.manjil.domain.disease.dto.response.DiseaseResponse;
import com.skthon.manjil.domain.disease.entity.Disease;
import com.skthon.manjil.domain.disease.mapper.DiseaseMapper;
import com.skthon.manjil.domain.disease.repository.DiseaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiseaseService {

  private final DiseaseRepository diseaseRepository;
  private final DiseaseMapper diseaseMapper;

  public DiseaseResponse create(DiseaseRequest req) {
    Disease disease = diseaseMapper.toEntity(req);
    Disease saved = diseaseRepository.save(disease);
    return diseaseMapper.toResponse(saved);
  }
}

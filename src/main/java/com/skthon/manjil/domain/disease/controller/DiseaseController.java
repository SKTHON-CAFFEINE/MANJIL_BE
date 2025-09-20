package com.skthon.manjil.domain.disease.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diseases")
@Tag(name = "Disease", description = "Disease 관련 API")
public class DiseaseController {}

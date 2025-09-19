package com.skthon.manjil.domain.disease.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diseases")
@Tag(name = "Disease", description = "Disease 관련 API")
public class DiseaseController {

}

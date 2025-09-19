package com.skthon.manjil.domain.recommend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
@Tag(name = "Recommend", description = "Recommend 관련 API")
public class RecommendController {

}

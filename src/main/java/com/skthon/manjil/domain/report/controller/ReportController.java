package com.skthon.manjil.domain.report.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "Report", description = "Report 관련 API")
public class ReportController {

}

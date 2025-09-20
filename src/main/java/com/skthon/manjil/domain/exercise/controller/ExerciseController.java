package com.skthon.manjil.domain.exercise.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
@Tag(name = "Exercise", description = "Exercise 관련 API")
public class ExerciseController {}

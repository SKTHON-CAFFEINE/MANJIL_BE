package com.skthon.manjil.domain.exercise.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skthon.manjil.domain.exercise.dto.request.ExerciseRequest;
import com.skthon.manjil.domain.exercise.dto.response.ExerciseResponse;
import com.skthon.manjil.domain.exercise.dto.response.ExerciseResponse.Detail;
import com.skthon.manjil.domain.exercise.entity.Exercise;
import com.skthon.manjil.domain.exercise.entity.ExerciseDetail;

@Component
public class ExerciseMapper {

  public Exercise toEntity(ExerciseRequest.CreateRequest req) {
    return Exercise.builder().name(req.getName()).unit(req.getUnit()).build();
  }

  public ExerciseDetail toDetailEntity(Exercise exercise, String imageUrl, String description) {
    return ExerciseDetail.builder()
        .exercise(exercise)
        .imageUrl(imageUrl)
        .description(description)
        .build();
  }

  public ExerciseResponse toResponse(Exercise exercise) {
    List<Detail> details =
        exercise.getDetails().stream()
            .map(
                d ->
                    ExerciseResponse.Detail.builder()
                        .id(d.getId())
                        .imageUrl(d.getImageUrl())
                        .description(d.getDescription())
                        .build())
            .toList();

    return ExerciseResponse.builder()
        .id(exercise.getId())
        .name(exercise.getName())
        .unit(exercise.getUnit())
        .details(details)
        .build();
  }
}

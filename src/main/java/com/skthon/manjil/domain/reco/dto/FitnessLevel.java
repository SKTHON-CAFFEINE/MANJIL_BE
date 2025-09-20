package com.skthon.manjil.domain.reco.dto;

import java.util.Arrays;

public enum FitnessLevel {
  STRETCH(1),
  LIGHT_STRENGTH(2),
  REGULAR(3),
  ACTIVE(4);

  private final int code;

  FitnessLevel(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static FitnessLevel fromCode(int code) {
    return Arrays.stream(values()).filter(f -> f.code == code).findFirst().orElseThrow();
  }
}

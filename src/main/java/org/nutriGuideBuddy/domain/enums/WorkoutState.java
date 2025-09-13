package org.nutriGuideBuddy.domain.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum WorkoutState {
  SEDENTARY,
  LIGHTLY_ACTIVE,
  MODERATELY_ACTIVE,
  VERY_ACTIVE,
  SUPER_ACTIVE;

  public static String validValues() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
  }
}

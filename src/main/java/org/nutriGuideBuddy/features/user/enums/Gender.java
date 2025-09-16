package org.nutriGuideBuddy.features.user.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Gender {
  MALE,
  FEMALE;

  public static String validValues() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
  }
}

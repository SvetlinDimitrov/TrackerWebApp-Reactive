package org.nutriGuideBuddy.features.user.enums;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
  ADMIN,
  USER;

  public static List<String> validValues() {
    return Arrays.stream(values()).map(Enum::name).toList();
  }
}

package org.nutriGuideBuddy.features.tracker.dto;

import java.time.LocalDate;

public record CalorieRequest(LocalDate startDate, LocalDate endDate) {
  public CalorieRequest {
    LocalDate today = LocalDate.now();

    if (startDate == null && endDate == null) {
      startDate = today;
      endDate = today;
    } else if (startDate == null) {
      startDate = endDate;
    } else if (endDate == null) {
      endDate = startDate;
    }
  }
}

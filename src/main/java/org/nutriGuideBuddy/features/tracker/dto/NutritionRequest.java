package org.nutriGuideBuddy.features.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.nutriGuideBuddy.features.shared.annotations.ValidNutrientName;

public record NutritionRequest(
    @ValidNutrientName @NotBlank(message = "is required") String name,
    LocalDate startDate,
    LocalDate endDate) {
  public NutritionRequest {
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

package org.nutriGuideBuddy.features.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NutritionIntakeView {

  private String name;
  private Double dailyConsumed;
  private Double recommendedIntake;
  private String measurement;
}

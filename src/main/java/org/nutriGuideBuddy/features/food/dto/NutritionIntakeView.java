package org.nutriGuideBuddy.features.food.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class NutritionIntakeView {

  private String name;
  private BigDecimal dailyConsumed;
  private BigDecimal recommendedIntake;
  private String measurement;
}

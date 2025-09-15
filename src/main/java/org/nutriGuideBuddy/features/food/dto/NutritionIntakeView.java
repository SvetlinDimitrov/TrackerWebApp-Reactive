package org.nutriGuideBuddy.features.food.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NutritionIntakeView {

  private String name;
  private BigDecimal dailyConsumed;
  private BigDecimal recommendedIntake;
  private String measurement;
}

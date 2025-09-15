package org.nutriGuideBuddy.features.food.dto;

import java.math.BigDecimal;
import org.nutriGuideBuddy.features.food.entity.Calorie;

public record CalorieView(BigDecimal amount, String unit) {

  public static CalorieView toView(Calorie entity) {
    return new CalorieView(entity.getAmount(), entity.getUnit());
  }
}

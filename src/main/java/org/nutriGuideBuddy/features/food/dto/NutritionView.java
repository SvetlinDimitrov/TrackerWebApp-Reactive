package org.nutriGuideBuddy.features.food.dto;

import java.math.BigDecimal;
import org.nutriGuideBuddy.features.food.entity.Nutrition;

public record NutritionView(String name, String unit, BigDecimal amount) {

  public static NutritionView toView(Nutrition entity) {
    return new NutritionView(entity.getName(), entity.getUnit(), entity.getAmount());
  }
}

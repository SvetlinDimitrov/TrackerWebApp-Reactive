package org.nutriGuideBuddy.features.food.dto;

import org.nutriGuideBuddy.features.food.entity.Nutrition;

import java.math.BigDecimal;

public record NutritionView(String name, String unit, BigDecimal amount) {

  public static NutritionView toView(Nutrition entity) {
    return new NutritionView(entity.getName(), entity.getUnit(), entity.getAmount());
  }
}

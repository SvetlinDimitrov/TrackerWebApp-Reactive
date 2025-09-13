package org.nutriGuideBuddy.features.food.dto;

import org.nutriGuideBuddy.features.food.entity.Calorie;

import java.math.BigDecimal;

public record CalorieView(BigDecimal amount, String unit) {

  public static CalorieView toView(Calorie entity) {
    return new CalorieView(entity.getAmount(), entity.getUnit());
  }
}

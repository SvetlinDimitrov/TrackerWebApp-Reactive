package org.nutriGuideBuddy.features.food.dto;

import org.nutriGuideBuddy.features.food.entity.CalorieEntity;

import java.math.BigDecimal;

public record CalorieView(BigDecimal amount, String unit) {

  public static CalorieView toView(CalorieEntity entity) {
    return new CalorieView(entity.getAmount(), entity.getUnit());
  }
}

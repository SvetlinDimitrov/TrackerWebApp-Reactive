package org.nutriGuideBuddy.features.food.enums;

import lombok.Getter;

@Getter
public enum AllowedCalorieUnits {
  CALORIE("kcal");
  private final String symbol;

  AllowedCalorieUnits(String symbol) {
    this.symbol = symbol;
  }
}

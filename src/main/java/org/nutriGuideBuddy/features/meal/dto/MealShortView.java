package org.nutriGuideBuddy.features.meal.dto;

import org.nutriGuideBuddy.features.food.dto.ShortenFood;
import org.nutriGuideBuddy.features.meal.entity.MealEntity;

import java.math.BigDecimal;
import java.util.List;

public record MealShortView(
    String id, String name, BigDecimal consumedCalories, List<ShortenFood> foods) {

  public static MealShortView toView(
      MealEntity entity, List<ShortenFood> foodView, BigDecimal caloriesConsumed) {
    return new MealShortView(entity.getId(), entity.getName(), caloriesConsumed, foodView);
  }
}

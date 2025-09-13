package org.nutriGuideBuddy.features.meal.dto;

import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.meal.entity.Meal;

import java.math.BigDecimal;
import java.util.List;

public record MealView(String id, String name, BigDecimal consumedCalories, List<FoodView> foods) {

  public static MealView toView(
      Meal entity, List<FoodView> foodView, BigDecimal caloriesConsumed) {
    return new MealView(entity.getId(), entity.getName(), caloriesConsumed, foodView);
  }
}

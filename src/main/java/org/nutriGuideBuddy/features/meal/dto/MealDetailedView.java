package org.nutriGuideBuddy.features.meal.dto;

import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.meal.entity.Meal;

import java.math.BigDecimal;
import java.util.List;

public record MealDetailedView(Long id, String name, BigDecimal consumedCalories, List<FoodView> foods) {

  public static MealDetailedView toView(Meal entity, List<FoodView> foodView, BigDecimal caloriesConsumed) {
    return new MealDetailedView(entity.getId(), entity.getName(), caloriesConsumed, foodView);
  }
}

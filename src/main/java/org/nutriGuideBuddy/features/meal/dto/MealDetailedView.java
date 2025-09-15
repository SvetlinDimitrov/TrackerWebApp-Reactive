package org.nutriGuideBuddy.features.meal.dto;

import java.math.BigDecimal;
import java.util.List;
import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.meal.entity.Meal;

public record MealDetailedView(Long id, String name, BigDecimal consumedCalories, List<FoodView> foods) {

  public static MealDetailedView toView(Meal entity, List<FoodView> foodView, BigDecimal caloriesConsumed) {
    return new MealDetailedView(entity.getId(), entity.getName(), caloriesConsumed, foodView);
  }
}

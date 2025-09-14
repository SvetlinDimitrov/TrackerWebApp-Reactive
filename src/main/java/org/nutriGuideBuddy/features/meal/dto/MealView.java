package org.nutriGuideBuddy.features.meal.dto;

import java.math.BigDecimal;
import java.util.List;
import org.nutriGuideBuddy.features.food.dto.FoodShortView;

public record MealView(
    Long id, Long userId, String name, BigDecimal consumedCalories, List<FoodShortView> foods) {}

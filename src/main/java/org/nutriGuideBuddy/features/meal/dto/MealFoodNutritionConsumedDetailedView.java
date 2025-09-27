package org.nutriGuideBuddy.features.meal.dto;

import java.util.Set;

public record MealFoodNutritionConsumedDetailedView(
    Long id, String name, String unit, Set<MealFoodNutritionConsumedView> consumed) {}

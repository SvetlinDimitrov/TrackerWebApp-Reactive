package org.nutriGuideBuddy.features.meal.dto;

public record MealFoodNutritionConsumedView(
    Long mealId, String mealName, Long foodId, String foodName, Double amount) {}

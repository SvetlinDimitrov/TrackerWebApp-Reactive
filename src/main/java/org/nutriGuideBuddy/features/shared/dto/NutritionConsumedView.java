package org.nutriGuideBuddy.features.shared.dto;

public record NutritionConsumedView(
    Long mealId, String mealName, Long foodId, String foodName, Double amount) {}

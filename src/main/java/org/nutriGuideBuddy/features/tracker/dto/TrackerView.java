package org.nutriGuideBuddy.features.tracker.dto;

import java.util.Set;

public record TrackerView(
    Double calorieGoal,
    Double caloriesConsumed,
    Set<NutritionIntakeView> vitamins,
    Set<NutritionIntakeView> minerals,
    Set<NutritionIntakeView> macronutrients) {}

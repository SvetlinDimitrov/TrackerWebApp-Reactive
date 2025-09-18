package org.nutriGuideBuddy.features.tracker.dto;

import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;

import java.util.List;
import java.util.Set;

public record TrackerView(
    Double calorieGoal,
    List<MealConsumedView> consumed,
    Set<NutritionIntakeView> vitamins,
    Set<NutritionIntakeView> minerals,
    Set<NutritionIntakeView> macronutrients) {}

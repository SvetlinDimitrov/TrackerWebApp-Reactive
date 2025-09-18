package org.nutriGuideBuddy.features.tracker.dto;

import java.util.List;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;

public record TrackerView(
    Double calorieGoal, List<MealConsumedView> consumed, Set<NutritionIntakeView> nutrients) {}

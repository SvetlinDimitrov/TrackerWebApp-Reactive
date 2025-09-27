package org.nutriGuideBuddy.features.tracker.dto;

import java.util.Set;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;

/**
 * View model representing intake progress for a nutrient.
 *
 * @param nutrient the nutrient name (e.g. "Vitamin C", "Protein")
 * @param consumed amount consumed so far
 * @param recommended recommended daily intake
 * @param maxRecommended tolerable upper intake level (if defined)
 * @param unit measurement unit (e.g. g, mg, µg, kcal)
 */
public record NutritionIntakeView(
    String nutrient,
    Set<MealFoodNutritionConsumedView> consumed,
    Double recommended,
    Double maxRecommended,
    String unit) {}

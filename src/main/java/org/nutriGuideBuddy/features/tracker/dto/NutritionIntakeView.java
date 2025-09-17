package org.nutriGuideBuddy.features.tracker.dto;

/**
 * View model representing intake progress for a nutrient.
 *
 * @param nutrient the nutrient name (e.g. "Vitamin C", "Protein")
 * @param consumed amount consumed so far
 * @param recommended recommended daily intake
 * @param unit measurement unit (e.g. g, mg, µg, kcal)
 */
public record NutritionIntakeView(
    String nutrient, Double consumed, Double recommended, String unit) {}

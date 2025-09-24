package org.nutriGuideBuddy.features.shared.dto;

import java.util.Set;

public record FoodView(
    Long id,
    String name,
    String info,
    String largeInfo,
    String picture,
    Double calorieAmount,
    String calorieUnit,
    Set<ServingView> servings,
    Set<NutritionView> nutritions) {}

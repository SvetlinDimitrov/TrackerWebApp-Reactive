package org.nutriGuideBuddy.features.meal.dto;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;

public record MealFoodView(
    Long id,
    Long mealId,
    String name,
    String info,
    String largeInfo,
    String picture,
    Double calorieAmount,
    String calorieUnit,
    Set<ServingView> servings,
    Set<NutritionView> nutrients) {}

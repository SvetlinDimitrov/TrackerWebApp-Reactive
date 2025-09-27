package org.nutriGuideBuddy.features.custom_food.dto;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;

public record CustomFoodView(
    Long id,
    String name,
    String info,
    String largeInfo,
    String picture,
    Double calorieAmount,
    String calorieUnit,
    Set<ServingView> servings,
    Set<NutritionView> nutrients) {}

package org.nutriGuideBuddy.features.meal.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.nutriGuideBuddy.features.shared.dto.FoodView;

public record MealFoodView(@JsonUnwrapped FoodView baseFood, Long mealId) {}

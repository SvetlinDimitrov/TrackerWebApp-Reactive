package org.nutriGuideBuddy.features.meal.dto;

import java.time.LocalDate;
import java.util.List;

public record MealView(
    Long id,
    Long userId,
    String name,
    LocalDate createdAt,
    LocalDate updatedAt,
    Double totalCalories,
    List<MealFoodShortView> foods) {}

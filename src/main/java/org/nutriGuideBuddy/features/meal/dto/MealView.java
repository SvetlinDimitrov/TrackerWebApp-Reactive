package org.nutriGuideBuddy.features.meal.dto;

import java.time.LocalDate;
import java.util.List;
import org.nutriGuideBuddy.features.shared.dto.FoodShortView;

public record MealView(
    Long id,
    Long userId,
    String name,
    LocalDate createdAt,
    LocalDate updateAt,
    Double totalCalories,
    List<FoodShortView> foods) {}

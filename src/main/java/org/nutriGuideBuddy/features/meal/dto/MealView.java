package org.nutriGuideBuddy.features.meal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.nutriGuideBuddy.features.food.dto.FoodShortView;

public record MealView(
    Long id,
    Long userId,
    String name,
    LocalDate createdAt,
    LocalDate updateAt,
    BigDecimal consumedCalories,
    List<FoodShortView> foods) {}

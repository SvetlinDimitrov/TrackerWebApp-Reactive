package org.nutriGuideBuddy.features.food.dto;

import java.math.BigDecimal;

public record FoodShortView(Long id, String name, BigDecimal calories) {}

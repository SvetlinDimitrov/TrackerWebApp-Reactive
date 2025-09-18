package org.nutriGuideBuddy.features.shared.dto;

import java.util.Set;

public record MealConsumedView(Long id, String name, Double amount, Set<FoodConsumedView> foods) {}

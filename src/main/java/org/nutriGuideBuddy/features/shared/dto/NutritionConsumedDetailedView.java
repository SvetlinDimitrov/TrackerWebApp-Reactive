package org.nutriGuideBuddy.features.shared.dto;

import java.util.Set;

public record NutritionConsumedDetailedView(
    Long id, String name, String unit, Set<NutritionConsumedView> consumed) {}

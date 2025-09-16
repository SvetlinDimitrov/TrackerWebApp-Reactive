package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.nutriGuideBuddy.features.shared.annotaions.AllowedNutrientName;
import org.nutriGuideBuddy.features.shared.annotaions.AllowedNutrientUnit;

@AllowedNutrientUnit
public record NutritionUpdateRequest(
    @NotNull(message = "is required") Long id,
    @AllowedNutrientName String name,
    String unit,
    @DecimalMin(value = "0.001", message = "must be higher than 0") Double amount) {}

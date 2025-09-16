package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.nutriGuideBuddy.features.shared.annotaions.AllowedNutrient;

public record NutritionUpdateRequest(
    @NotNull(message = "is required") Long id,
    @AllowedNutrient String name,
    String unit,
    @DecimalMin(value = "0.01", message = "must be higher than 0") Double amount) {}

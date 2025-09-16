package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.nutriGuideBuddy.features.shared.annotaions.AllowedNutrient;

public record NutritionCreateRequest(
    @NotNull(message = "name is required") @AllowedNutrient String name,
    @NotNull(message = "is required") String unit,
    @NotNull(message = "is required") @DecimalMin(value = "0.01", message = "must be higher than 0")
        Double amount) {}

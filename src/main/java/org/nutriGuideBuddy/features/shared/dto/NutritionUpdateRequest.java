package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record NutritionUpdateRequest(
    @NotNull(message = "is required") Long id,
    @DecimalMin(value = "0.001", message = "must be higher than 0") Double amount) {}

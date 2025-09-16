package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServingUpdateRequest(
    @NotNull(message = "is required") Long id,
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double amount,
    @DecimalMin(value = "0.1", message = "weight must be greater than 0") Double servingWeight,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String metric,
    Boolean main) {}

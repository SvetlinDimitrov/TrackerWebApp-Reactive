package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServingCreateRequest(
    @NotNull(message = "is required") @DecimalMin(value = "0.1", message = "must be greater than 0")
        Double amount,
    @NotNull(message = "weight is required")
        @DecimalMin(value = "0.1", message = "weight must be greater than 0")
        Double servingWeight,
    @NotNull(message = "is required")
        @Size(min = 1, max = 255, message = "must be between 1 and 255 characters")
        String metric,
    @NotNull(message = "is required") Boolean main) {}

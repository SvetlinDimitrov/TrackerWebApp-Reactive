package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServingUpdateRequest(
    @NotNull(message = "is required") Long id,
    Boolean main,
    @Size(min = 1, max = 50, message = "must be between 1 and 50 characters") String metric,
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double amount,
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double gramsTotal) {}

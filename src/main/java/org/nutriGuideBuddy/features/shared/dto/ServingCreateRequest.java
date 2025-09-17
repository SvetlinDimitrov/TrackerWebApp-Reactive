package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.nutriGuideBuddy.features.shared.annotaions.ValidServingMetric;

public record ServingCreateRequest(
    @NotNull(message = "is required") @DecimalMin(value = "0.1", message = "must be greater than 0")
        Double amount,
    @NotNull(message = "is required") @ValidServingMetric String metric,
    @NotNull(message = "is required") Boolean main) {}

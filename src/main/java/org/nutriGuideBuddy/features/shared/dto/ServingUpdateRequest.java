package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.nutriGuideBuddy.features.shared.annotaions.ValidServingMetric;

public record ServingUpdateRequest(
    @NotNull(message = "is required") Long id,
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double amount,
    @ValidServingMetric String metric,
    Boolean main) {}

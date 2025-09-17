package org.nutriGuideBuddy.features.shared.dto;

import org.nutriGuideBuddy.features.shared.enums.ServingMetric;

public record ServingView(Long id, Double amount, ServingMetric metric, Boolean main) {}

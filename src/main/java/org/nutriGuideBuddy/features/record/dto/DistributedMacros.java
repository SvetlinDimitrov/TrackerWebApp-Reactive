package org.nutriGuideBuddy.features.record.dto;

import java.math.BigDecimal;

public record DistributedMacros(
    BigDecimal protein, BigDecimal fat, BigDecimal carbs, BigDecimal omega6, BigDecimal omega3) {}

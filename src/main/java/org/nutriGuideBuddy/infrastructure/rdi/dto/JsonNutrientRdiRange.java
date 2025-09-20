package org.nutriGuideBuddy.infrastructure.rdi.dto;

import java.util.Optional;

public record JsonNutrientRdiRange(
    double ageMin,
    double ageMax,
    Optional<Double> rdiMin,
    Optional<Double> rdiMax,
    String unit,
    boolean isDerived,
    Optional<RdiBasis> basis,
    Optional<Double> divisor) {}

package org.nutriGuideBuddy.infrastructure.rdi.dto;

/** Basis for calculating nutrient reference intakes. */
public enum RdiBasis {
  ENERGY, // % of energy OR per kcal (fiber), needs kcal + divisor
  BODY_WEIGHT // g/kg/day
}

package org.nutriGuideBuddy.features.user.enums;

/**
 * Authorities / frameworks for nutrient recommendations.
 *
 * <p>Users can select which baseline guideline they want to use (before applying diet-specific
 * overlays like Keto, DASH, etc.).
 */
public enum NutritionAuthority {

  /** NIH / IOM (USA) Dietary Reference Intakes (DRIs: RDA, AI, UL) */
  NIH_IOM,

  /** World Health Organization / FAO nutrient intake goals (global reference) */
  WHO_FAO,

  /** European Food Safety Authority (EFSA) Dietary Reference Values (DRVs) */
  EFSA,

  /** UK Scientific Advisory Committee on Nutrition (SACN) Reference Nutrient Intakes (RNIs) */
  UK_SACN,

  /** Germany / Austria / Switzerland joint recommendations (D-A-CH reference values) */
  DACH,
}

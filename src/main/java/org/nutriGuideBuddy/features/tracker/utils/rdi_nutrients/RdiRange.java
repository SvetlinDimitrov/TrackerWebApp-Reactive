package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Represents a recommended dietary intake (RDI) rule for a nutrient, based on age and optionally
 * gender.
 *
 * <p>If gender is null, the rule applies to both males and females.
 */
public record RdiRange(int minAge, int maxAge, Gender gender, double value) {}

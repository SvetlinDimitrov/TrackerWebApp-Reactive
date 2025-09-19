package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Represents a recommended dietary intake (RDI) rule for a nutrient, based on age, gender, and life
 * stage (including pregnancy/lactation).
 *
 * <p>Includes: - recommended value (RDA/AI) - tolerable upper intake level (UL), if applicable
 *
 * <p>If gender is null, the rule applies to both males and females.
 */
public record RdiRange(
    int minAge,
    int maxAge,
    Gender gender,
    double recommended,
    Double upperLimit // can be null if no UL exists
    ) {}

package org.nutriGuideBuddy.infrastructure.rdi.utils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.dto.RdiBasis;

public final class RdiFinder {

  private RdiFinder() {}

  /**
   * Finds and resolves a nutrient RDI range for a user.
   *
   * @param groupMap requirements by population group
   * @param gender user gender
   * @param age user age (years)
   * @param energy estimated daily energy requirement (kcal/day)
   * @param bodyWeight user body weight (kg) - needed for BODY_WEIGHT based nutrients
   * @return resolved JsonNutrientRdiRange with absolute values if derived, or stored values if
   *     fixed
   */
  public static JsonNutrientRdiRange findMatch(
      Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> groupMap,
      Gender gender,
      int age,
      double energy,
      double bodyWeight) {

    if (groupMap == null || groupMap.isEmpty()) return null;

    JsonPopulationGroup group = mapGenderToPopulationGroup(gender);

    // Try gender-specific group
    JsonNutrientRdiRange match = findForGroup(groupMap.get(group), age);
    if (match != null) return resolve(match, energy, bodyWeight);

    // Fallback: ALL group
    match = findForGroup(groupMap.get(JsonPopulationGroup.ALL), age);
    if (match != null) return resolve(match, energy, bodyWeight);

    return null;
  }

  private static JsonNutrientRdiRange findForGroup(Set<JsonNutrientRdiRange> ranges, int age) {
    if (ranges == null) return null;
    return ranges.stream()
        .filter(r -> age >= r.ageMin() && age <= r.ageMax())
        .findFirst()
        .orElse(null);
  }

  private static JsonNutrientRdiRange resolve(
      JsonNutrientRdiRange range, double energy, double bodyWeight) {

    if (!range.isDerived()) {
      return range; // static values
    }

    if (range.basis().isEmpty()) {
      return range; // no basis → nothing to calculate
    }

    RdiBasis basis = range.basis().get();

    double divisor = range.divisor().orElse(1.0);

    Optional<Double> rdiMin = range.rdiMin();
    Optional<Double> rdiMax = range.rdiMax();

    Double resolvedMin = null;
    Double resolvedMax = null;

    switch (basis) {
      case ENERGY -> {
        if (divisor == 1000) {
          // Per 1000 kcal, e.g. Fiber
          if (rdiMin.isPresent()) {
            resolvedMin = (energy / divisor) * rdiMin.get();
          }
          if (rdiMax.isPresent()) {
            resolvedMax = (energy / divisor) * rdiMax.get();
          }
        } else {
          // % of energy, e.g. macros
          if (rdiMin.isPresent()) {
            resolvedMin = (energy * rdiMin.get() / 100.0) / divisor;
          }
          if (rdiMax.isPresent()) {
            resolvedMax = (energy * rdiMax.get() / 100.0) / divisor;
          }
        }
      }
      case BODY_WEIGHT -> {
        if (rdiMin.isPresent()) {
          resolvedMin = bodyWeight * rdiMin.get();
        }
        if (rdiMax.isPresent()) {
          resolvedMax = bodyWeight * rdiMax.get();
        }
      }
    }

    return new JsonNutrientRdiRange(
        range.ageMin(),
        range.ageMax(),
        Optional.ofNullable(resolvedMin),
        Optional.ofNullable(resolvedMax),
        range.unit(),
        true,
        range.basis(),
        range.divisor(),
        range.note());
  }

  private static JsonPopulationGroup mapGenderToPopulationGroup(Gender gender) {
    return switch (gender) {
      case MALE -> JsonPopulationGroup.MALE;
      case FEMALE -> JsonPopulationGroup.FEMALE;
      case FEMALE_PREGNANT -> JsonPopulationGroup.FEMALE_PREGNANT;
      case FEMALE_LACTATING -> JsonPopulationGroup.FEMALE_LACTATING;
    };
  }
}

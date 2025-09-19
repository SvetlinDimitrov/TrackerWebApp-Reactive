package org.nutriGuideBuddy.infrastructure.io.rdi;

import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonRdiRange;

public final class RdiFinder {

  private RdiFinder() {}

  public static JsonRdiRange findMatch(
      Map<JsonPopulationGroup, Set<JsonRdiRange>> groupMap, Gender gender, int age) {
    if (groupMap == null || groupMap.isEmpty()) return null;

    JsonPopulationGroup group = mapGenderToPopulationGroup(gender);

    if (groupMap.containsKey(group)) {
      JsonRdiRange match =
          groupMap.get(group).stream()
              .filter(r -> age >= r.ageMin() && age <= r.ageMax())
              .findFirst()
              .orElse(null);
      if (match != null) return match;
    }

    if (groupMap.containsKey(JsonPopulationGroup.ALL)) {
      return groupMap.get(JsonPopulationGroup.ALL).stream()
          .filter(r -> age >= r.ageMin() && age <= r.ageMax())
          .findFirst()
          .orElse(null);
    }

    return null;
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

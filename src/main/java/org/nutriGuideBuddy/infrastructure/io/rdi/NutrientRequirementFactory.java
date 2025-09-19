package org.nutriGuideBuddy.infrastructure.io.rdi;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonRdiRange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NutrientRequirementFactory {

  private final NutrientAuthorityStore nutrientAuthorityStore;
  private final DietAuthorityStore dietAuthorityStore;

  /**
   * Build merged nutrient requirements for a given authority + diet. Cover values override
   * baseline.
   */
  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>> build(
      NutritionAuthority authority, DietType diet) {

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>> baseline =
        nutrientAuthorityStore.getRequirements(authority);

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>> cover =
        dietAuthorityStore.getRequirements(diet, authority);

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>> merged =
        new EnumMap<>(JsonAllowedNutrients.class);

    if (baseline != null) {
      baseline.forEach(
          (nutrient, groupMap) -> {
            Map<JsonPopulationGroup, Set<JsonRdiRange>> copy =
                new EnumMap<>(JsonPopulationGroup.class);
            groupMap.forEach((group, ranges) -> copy.put(group, new HashSet<>(ranges)));
            merged.put(nutrient, copy);
          });
    }

    if (cover != null) {
      cover.forEach(
          (nutrient, groupMap) -> {
            Map<JsonPopulationGroup, Set<JsonRdiRange>> existing =
                merged.computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class));
            groupMap.forEach((group, ranges) -> existing.put(group, new HashSet<>(ranges)));
          });
    }

    return merged;
  }
}

package org.nutriGuideBuddy.infrastructure.rdi;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NutrientRequirementFactory {

  private final NutrientAuthorityStore nutrientAuthorityStore;
  private final DietAuthorityStore dietAuthorityStore;

  /**
   * Build merged nutrient requirements for a given nutritionAuthority + diet. Baseline values (from
   * nutritionAuthority) are merged with diet covers. Covers always override baseline for the same
   * nutrient+group.
   */
  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> build(
      NutritionAuthority authority, DietType diet) {

    // baseline from the selected nutritionAuthority
    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> baseline =
        nutrientAuthorityStore.getRequirements(authority);

    // overlay cover for given diet + nutritionAuthority
    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> cover =
        dietAuthorityStore.getRequirements(diet, authority);

    // merged result
    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> merged =
        new EnumMap<>(JsonAllowedNutrients.class);

    if (baseline != null) {
      baseline.forEach(
          (nutrient, groupMap) -> {
            Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> copy =
                new EnumMap<>(JsonPopulationGroup.class);
            groupMap.forEach((group, ranges) -> copy.put(group, new HashSet<>(ranges)));
            merged.put(nutrient, copy);
          });
    }

    if (cover != null) {
      cover.forEach(
          (nutrient, groupMap) -> {
            Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> existing =
                merged.computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class));
            groupMap.forEach((group, ranges) -> existing.put(group, new HashSet<>(ranges)));
          });
    }

    return merged;
  }
}

package org.nutriGuideBuddy.infrastructure.rdi.utils;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonDietType;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.springframework.stereotype.Component;

@Slf4j
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

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> result =
        new EnumMap<>(JsonAllowedNutrients.class);

    if (authority == null) {
      log.warn("No NutritionAuthority provided; returning empty requirements.");
      return result;
    }
    JsonNutritionAuthority jsonAuth;
    try {
      jsonAuth = JsonNutritionAuthority.valueOf(authority.name());
    } catch (IllegalArgumentException ex) {
      log.warn(
          "Unknown NutritionAuthority '{}': cannot map to JsonNutritionAuthority. Returning empty.",
          authority,
          ex);
      return result;
    }

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> baseline =
        nutrientAuthorityStore.getRequirements(jsonAuth);

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> merged =
        deepCopy(baseline);

    if (diet == null || diet == DietType.NONE) {
      return merged;
    }

    JsonDietType jsonDiet;
    try {
      jsonDiet = JsonDietType.valueOf(diet.name());
    } catch (IllegalArgumentException ex) {
      log.warn("Unknown DietType '{}' cannot map to JsonDietType. Skipping covers.", diet, ex);
      return merged;
    }

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> cover =
        dietAuthorityStore.getRequirements(jsonDiet, authority);

    if (cover == null || cover.isEmpty()) {
      return merged;
    }

    for (var nutrientEntry : cover.entrySet()) {
      Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> targetGroups =
          merged.computeIfAbsent(
              nutrientEntry.getKey(), k -> new EnumMap<>(JsonPopulationGroup.class));

      for (var groupEntry : nutrientEntry.getValue().entrySet()) {
        targetGroups.put(groupEntry.getKey(), new HashSet<>(groupEntry.getValue()));
      }
    }

    return merged;
  }

  private Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> deepCopy(
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> source) {

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> copy =
        new EnumMap<>(JsonAllowedNutrients.class);

    if (source == null || source.isEmpty()) return copy;

    for (var nutrientEntry : source.entrySet()) {
      Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> groupMapCopy =
          new EnumMap<>(JsonPopulationGroup.class);
      for (var groupEntry : nutrientEntry.getValue().entrySet()) {
        groupMapCopy.put(groupEntry.getKey(), new HashSet<>(groupEntry.getValue()));
      }
      copy.put(nutrientEntry.getKey(), groupMapCopy);
    }

    return copy;
  }
}

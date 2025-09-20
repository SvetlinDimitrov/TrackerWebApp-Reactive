package org.nutriGuideBuddy.infrastructure.rdi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DietAuthorityStore {

  private final Map<
          DietType,
          Map<
              JsonNutritionAuthority,
              Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>>
      store = new EnumMap<>(DietType.class);

  private final Map<
          DietType, Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      baselineStore = new EnumMap<>(DietType.class);

  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void init() {
    for (DietType dietType : DietType.values()) {
      loadDietFile(dietType, "others.json");
      loadDietFile(dietType, "others.json");
    }
  }

  private void loadDietFile(DietType dietType, String fileName) {
    String path = String.format("rdi/covers/%s/%s", dietType.name(), fileName);

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
      if (inputStream == null) {
        log.debug("No cover file found for: {}", path);
        return;
      }

      JsonNode root = mapper.readTree(inputStream);

      JsonNode baselineNode = root.get("baseline");
      JsonNode overlaysNode = root.get("overlays");

      // baseline is universal for the diet
      if (baselineNode != null && baselineNode.isObject()) {
        Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
            baselineRequirements =
                baselineStore.computeIfAbsent(
                    dietType, k -> new EnumMap<>(JsonAllowedNutrients.class));

        parseNutrientsInto(baselineNode, baselineRequirements, path);
      }

      // overlays are per nutritionAuthority
      if (overlaysNode != null && overlaysNode.isObject()) {
        Map<
                JsonNutritionAuthority,
                Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
            authorityMap =
                store.computeIfAbsent(dietType, k -> new EnumMap<>(JsonNutritionAuthority.class));

        Iterator<String> authorityKeys = overlaysNode.fieldNames();
        while (authorityKeys.hasNext()) {
          String authorityKey = authorityKeys.next();

          JsonNutritionAuthority jsonAuth;
          try {
            jsonAuth = JsonNutritionAuthority.valueOf(authorityKey);
          } catch (IllegalArgumentException e) {
            log.warn("Skipping unknown nutritionAuthority {} in {}", authorityKey, path);
            continue;
          }

          JsonNode authorityNode = overlaysNode.get(authorityKey);

          Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
              nutrientRequirements =
                  authorityMap.computeIfAbsent(
                      jsonAuth, k -> new EnumMap<>(JsonAllowedNutrients.class));

          parseNutrientsInto(authorityNode, nutrientRequirements, path);
        }
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to load diet cover from " + path, e);
    }
  }

  private void parseNutrientsInto(
      JsonNode parentNode,
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> target,
      String path) {

    Iterator<String> nutrientNames = parentNode.fieldNames();
    while (nutrientNames.hasNext()) {
      String nutrientKey = nutrientNames.next();
      JsonAllowedNutrients nutrient = parseNutrient(nutrientKey);
      if (nutrient == null) continue;

      JsonNode nutrientNode = parentNode.get(nutrientKey);

      Iterator<String> groupNames = nutrientNode.fieldNames();
      while (groupNames.hasNext()) {
        String groupKey = groupNames.next();
        JsonPopulationGroup group = JsonPopulationGroup.valueOf(groupKey);

        JsonNode groupNode = nutrientNode.get(groupKey);

        Iterator<String> ageRanges = groupNode.fieldNames();
        while (ageRanges.hasNext()) {
          String ageRange = ageRanges.next();
          double[] bounds = parseAgeRange(ageRange);

          JsonNode values = groupNode.get(ageRange);

          Optional<Double> rdiMin =
              values.has("rdiMin") && !values.get("rdiMin").isNull()
                  ? Optional.of(values.get("rdiMin").asDouble())
                  : Optional.empty();
          Optional<Double> rdiMax =
              values.has("rdiMax") && !values.get("rdiMax").isNull()
                  ? Optional.of(values.get("rdiMax").asDouble())
                  : Optional.empty();
          String unit =
              values.has("unit") && !values.get("unit").isNull()
                  ? values.get("unit").asText()
                  : nutrient.getUnit();

          boolean isDerived = values.has("isDerived") && values.get("isDerived").asBoolean(false);
          Optional<RdiBasis> basis =
              values.has("basis") && !values.get("basis").isNull()
                  ? parseBasis(values.get("basis").asText())
                  : Optional.empty();
          Optional<Double> divisor =
              values.has("divisor") && !values.get("divisor").isNull()
                  ? Optional.of(values.get("divisor").asDouble())
                  : Optional.empty();

          JsonNutrientRdiRange requirement =
              new JsonNutrientRdiRange(
                  bounds[0], bounds[1], rdiMin, rdiMax, unit, isDerived, basis, divisor);

          target
              .computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class))
              .computeIfAbsent(group, k -> new HashSet<>())
              .add(requirement);
        }
      }
    }
  }

  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
      getRequirements(DietType diet, NutritionAuthority authority) {
    try {
      JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());

      // merge baseline + overlays
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> merged =
          new EnumMap<>(JsonAllowedNutrients.class);

      // 1. baseline first
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> baseline =
          baselineStore.getOrDefault(diet, Collections.emptyMap());
      deepMergeInto(merged, baseline);

      // 2. overlay (if exists) overrides
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> overlay =
          store
              .getOrDefault(diet, Collections.emptyMap())
              .getOrDefault(jsonAuth, Collections.emptyMap());
      deepMergeInto(merged, overlay);

      return merged;

    } catch (IllegalArgumentException e) {
      log.warn("No matching JSON nutritionAuthority for: {}", authority);
      return Collections.emptyMap();
    }
  }

  private void deepMergeInto(
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> target,
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> source) {
    for (var nutrientEntry : source.entrySet()) {
      target
          .computeIfAbsent(nutrientEntry.getKey(), k -> new EnumMap<>(JsonPopulationGroup.class))
          .putAll(nutrientEntry.getValue());
    }
  }

  private JsonAllowedNutrients parseNutrient(String key) {
    try {
      return JsonAllowedNutrients.valueOf(key);
    } catch (IllegalArgumentException e) {
      log.error("Skipping unknown nutrient: {}", key);
      return null;
    }
  }

  private Optional<RdiBasis> parseBasis(String text) {
    try {
      return Optional.of(RdiBasis.valueOf(text));
    } catch (IllegalArgumentException e) {
      log.error("Unknown RdiBasis value: {}", text);
      return Optional.empty();
    }
  }

  private double[] parseAgeRange(String range) {
    if (range == null || range.isBlank()) return new double[] {0, 0};
    String[] parts = range.split("-");
    if (parts.length == 2) {
      return new double[] {Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }
    throw new IllegalArgumentException("Invalid age range format: " + range);
  }
}

package org.nutriGuideBuddy.infrastructure.rdi.utils;

import com.fasterxml.jackson.databind.JsonNode;
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
public class DietAuthorityStore extends AbstractAuthorityStore {

  private final Map<
          DietType,
          Map<
              JsonNutritionAuthority,
              Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>>
      store = new EnumMap<>(DietType.class);

  private final Map<
          DietType, Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      baselineStore = new EnumMap<>(DietType.class);

  @PostConstruct
  private void init() {
    for (DietType dietType : DietType.values()) {
      loadDietFile(dietType, "nutrients.json");
      log.info("Loaded baseline nutrient file for diet: {}", dietType);
      loadDietFile(dietType, "others.json");
      log.info("Loaded other nutrient file for diet: {}", dietType);
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

      if (baselineNode != null) {
        Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> baseline =
            baselineStore.computeIfAbsent(dietType, k -> new EnumMap<>(JsonAllowedNutrients.class));
        parseNutrientsInto(baselineNode, baseline, path);
      }

      if (overlaysNode != null) {
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

          Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
              requirements =
                  authorityMap.computeIfAbsent(
                      jsonAuth, k -> new EnumMap<>(JsonAllowedNutrients.class));

          parseNutrientsInto(overlaysNode.get(authorityKey), requirements, path);
        }
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + path, e);
    }
  }

  private void parseNutrientsInto(
      JsonNode parentNode,
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> target,
      String context) {

    Iterator<String> nutrientNames = parentNode.fieldNames();
    while (nutrientNames.hasNext()) {
      String nutrientKey = nutrientNames.next();
      JsonAllowedNutrients nutrient = parseNutrient(nutrientKey, context);
      if (nutrient == null) continue;

      JsonNode nutrientNode = parentNode.get(nutrientKey);

      Iterator<String> groupNames = nutrientNode.fieldNames();
      while (groupNames.hasNext()) {
        String groupKey = groupNames.next();
        JsonPopulationGroup group = JsonPopulationGroup.valueOf(groupKey);
        JsonNode groupNode = nutrientNode.get(groupKey);

        if (!groupNode.isArray()) {
          log.warn(
              "Expected array for nutrient {} group {} in {}, skipping",
              nutrient.name(),
              groupKey,
              context);
          continue;
        }

        for (JsonNode entry : groupNode) {
          JsonNutrientRdiRange requirement = buildRange(entry, nutrient, context);
          if (requirement == null) continue;

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
    JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());

    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> merged =
        new EnumMap<>(JsonAllowedNutrients.class);

    deepMergeInto(merged, baselineStore.getOrDefault(diet, Collections.emptyMap()));
    deepMergeInto(
        merged,
        store
            .getOrDefault(diet, Collections.emptyMap())
            .getOrDefault(jsonAuth, Collections.emptyMap()));

    return merged;
  }

  private void deepMergeInto(
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> target,
      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>> source) {
    for (var entry : source.entrySet()) {
      target
          .computeIfAbsent(entry.getKey(), k -> new EnumMap<>(JsonPopulationGroup.class))
          .putAll(entry.getValue());
    }
  }
}

package org.nutriGuideBuddy.infrastructure.rdi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.infrastructure.rdi.dto.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NutrientAuthorityStore extends AbstractAuthorityStore {

  private final Map<
          JsonNutritionAuthority,
          Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      nutrientStore = new EnumMap<>(JsonNutritionAuthority.class);

  @PostConstruct
  private void init() {
    for (JsonNutritionAuthority authority : JsonNutritionAuthority.values()) {
      String path = String.format("rdi/%s/%s", authority.name(), "standard.json");

      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
        if (inputStream == null) {
          log.error("No resource found for: {}", path);
          continue;
        }

        JsonNode root = mapper.readTree(inputStream);
        if (root == null || !root.has("nutrients")) {
          log.error("Invalid JSON: missing 'nutrients' in {}", path);
          continue;
        }

        JsonNode nutrientsNode = root.get("nutrients");
        Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
            nutrientRequirements =
                nutrientStore.computeIfAbsent(
                    authority, k -> new EnumMap<>(JsonAllowedNutrients.class));

        parseNutrientsInto(nutrientsNode, nutrientRequirements, path);
        log.info("Loaded nutrient file for {}: {}", authority, "standard.json");

      } catch (IOException e) {
        log.error("Failed to load or parse JSON from {}", path, e);
      }
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
      if (nutrient == null) {
        log.error("Unknown nutrient {} in {}, skipping", nutrientKey, context);
        continue;
      }

      JsonNode nutrientNode = parentNode.get(nutrientKey);
      if (nutrientNode == null || !nutrientNode.isObject()) {
        log.error("Dropping nutrient {} in {}: expected object", nutrientKey, context);
        continue;
      }

      Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>> buffered =
          new EnumMap<>(JsonPopulationGroup.class);
      boolean dropNutrient = false;

      Iterator<String> groupNames = nutrientNode.fieldNames();
      while (groupNames.hasNext() && !dropNutrient) {
        String groupKey = groupNames.next();

        JsonPopulationGroup group = parsePopulationGroup(groupKey, context);
        if (group == null) {
          log.warn(
              "Dropping nutrient {} in {}: invalid population group '{}'",
              nutrientKey,
              context,
              groupKey);
          dropNutrient = true;
          break;
        }

        JsonNode groupNode = nutrientNode.get(groupKey);
        if (groupNode == null || !groupNode.isArray()) {
          log.warn(
              "Dropping nutrient {} in {}: expected array for group {}",
              nutrientKey,
              context,
              groupKey);
          dropNutrient = true;
          break;
        }

        for (JsonNode entry : groupNode) {
          JsonNutrientRdiRange requirement = buildRange(entry, nutrient, context);
          if (requirement == null) {
            dropNutrient = true;
            break;
          }

          buffered.computeIfAbsent(group, k -> new HashSet<>()).add(requirement);
        }
      }

      if (dropNutrient) {
        log.warn("Nutrient {} dropped from {} due to validation errors", nutrientKey, context);
        continue;
      }

      target
          .computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class))
          .putAll(buffered);
    }
  }

  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
      getRequirements(JsonNutritionAuthority authority) {
    JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());
    return nutrientStore.getOrDefault(jsonAuth, Collections.emptyMap());
  }
}

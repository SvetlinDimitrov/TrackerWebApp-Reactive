package org.nutriGuideBuddy.infrastructure.rdi;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
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
      loadFile(authority, "nutrients.json");
      loadFile(authority, "others.json");
    }
  }

  private void loadFile(JsonNutritionAuthority authority, String fileName) {
    String path = String.format("rdi/%s/%s", authority.name(), fileName);

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
      if (inputStream == null) {
        log.debug("No resource found for: {}", path);
        return;
      }

      JsonNode root = mapper.readTree(inputStream);
      JsonNode nutrientsNode = root.get("nutrients");
      if (nutrientsNode == null) {
        log.warn("Invalid JSON: missing 'nutrients' in {}", path);
        return;
      }

      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
          nutrientRequirements =
              nutrientStore.computeIfAbsent(
                  authority, k -> new EnumMap<>(JsonAllowedNutrients.class));

      parseNutrientsInto(nutrientsNode, nutrientRequirements, path);
      log.info("Loaded nutrient file for {}: {}", authority, fileName);

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
          log.warn("Expected array for group {} in {}, skipping", groupKey, context);
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
      getRequirements(NutritionAuthority authority) {
    JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());
    return nutrientStore.getOrDefault(jsonAuth, Collections.emptyMap());
  }
}

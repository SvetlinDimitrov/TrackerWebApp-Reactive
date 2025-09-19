package org.nutriGuideBuddy.infrastructure.io.rdi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonNutritionAuthority;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.io.rdi.dto.JsonRdiRange;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NutrientAuthorityStore {

  private final Map<
          JsonNutritionAuthority,
          Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>>>
      authorityStore = new EnumMap<>(JsonNutritionAuthority.class);
  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void init() {
    for (JsonNutritionAuthority authority : JsonNutritionAuthority.values()) {
      String path = String.format("rdi/%s/standard.json", authority.name());

      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
        if (inputStream == null) {
          log.error("No resource found for: {}", path);
          continue;
        }

        JsonNode root = mapper.readTree(inputStream);
        JsonNode nutrientsNode = root.get("nutrients");

        if (nutrientsNode == null) {
          throw new IllegalStateException("Invalid JSON: missing 'nutrients' node in " + path);
        }

        Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>>
            nutrientRequirements = new EnumMap<>(JsonAllowedNutrients.class);

        Iterator<String> nutrientNames = nutrientsNode.fieldNames();
        while (nutrientNames.hasNext()) {
          String nutrientKey = nutrientNames.next();
          JsonAllowedNutrients nutrient = parseNutrient(nutrientKey);
          if (nutrient == null) continue;

          JsonNode nutrientNode = nutrientsNode.get(nutrientKey);

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
              Double rdi =
                  values.has("rdi") && !values.get("rdi").isNull()
                      ? values.get("rdi").asDouble()
                      : null;
              Double ul =
                  values.has("ul") && !values.get("ul").isNull()
                      ? values.get("ul").asDouble()
                      : null;

              JsonRdiRange requirement = new JsonRdiRange(bounds[0], bounds[1], rdi, ul);

              nutrientRequirements
                  .computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class))
                  .computeIfAbsent(group, k -> new HashSet<>())
                  .add(requirement);
            }
          }
        }

        authorityStore.put(authority, nutrientRequirements);

      } catch (IOException e) {
        throw new RuntimeException("Failed to load nutrient requirements from " + path, e);
      }
    }
  }

  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonRdiRange>>> getRequirements(
      NutritionAuthority authority) {
    try {
      JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());
      return authorityStore.getOrDefault(jsonAuth, Collections.emptyMap());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("No matching JsonNutritionAuthority for " + authority, e);
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

  private double[] parseAgeRange(String range) {
    if (range == null || range.isBlank()) return new double[] {0, 0};
    String[] parts = range.split("-");
    if (parts.length == 2) {
      return new double[] {Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }
    throw new IllegalArgumentException("Invalid age range format: " + range);
  }
}

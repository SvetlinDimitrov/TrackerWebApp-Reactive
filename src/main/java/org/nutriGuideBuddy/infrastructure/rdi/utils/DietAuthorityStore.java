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
      String path = String.format("rdi/covers/%s/%s", dietType.name(), "standard.json");

      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
        if (inputStream == null) {
          log.error("No cover file found for: {}", path);
          continue;
        }

        JsonNode root = mapper.readTree(inputStream);
        if (root == null || !root.isObject()) {
          log.error("Invalid JSON root for {}", path);
          continue;
        }

        JsonNode baselineNode = root.get("baseline");
        JsonNode overlaysNode = root.get("overlays");

        if (baselineNode != null) {
          if (!baselineNode.isObject()) {
            log.warn("Expected 'baseline' to be an object in {}, skipping baseline", path);
          } else {
            Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
                baseline =
                    baselineStore.computeIfAbsent(
                        dietType, k -> new EnumMap<>(JsonAllowedNutrients.class));
            parseNutrientsInto(baselineNode, baseline, path);
          }
        } else {
          log.error("No 'baseline' section in {}", path);
        }

        if (overlaysNode != null) {
          if (!overlaysNode.isObject()) {
            log.warn("Expected 'overlays' to be an object in {}, skipping overlays", path);
          } else {
            Map<
                    JsonNutritionAuthority,
                    Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
                authorityMap =
                    store.computeIfAbsent(
                        dietType, k -> new EnumMap<>(JsonNutritionAuthority.class));

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

              JsonNode overlayNode = overlaysNode.get(authorityKey);
              if (overlayNode == null || !overlayNode.isObject()) {
                log.warn(
                    "Expected object for overlays[{}] in {}, skipping this authority",
                    authorityKey,
                    path);
                continue;
              }

              Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
                  requirements =
                      authorityMap.computeIfAbsent(
                          jsonAuth, k -> new EnumMap<>(JsonAllowedNutrients.class));

              parseNutrientsInto(overlayNode, requirements, path);
            }
          }
        } else {
          log.error("No 'overlays' section in {}", path);
        }

      } catch (IOException e) {
        log.error("Error loading cover file for: {}", path, e);
      }

      log.info("Loaded cover file for diet: {}", dietType);
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
        continue;
      }

      JsonNode nutrientNode = parentNode.get(nutrientKey);
      if (nutrientNode == null || !nutrientNode.isObject()) {
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
            log.warn(
                "Dropping nutrient {} in {}: invalid entry under group {} -> {}",
                nutrientKey,
                context,
                groupKey,
                entry);
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

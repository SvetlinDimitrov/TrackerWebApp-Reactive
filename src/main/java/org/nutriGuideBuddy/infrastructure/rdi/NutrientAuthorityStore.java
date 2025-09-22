package org.nutriGuideBuddy.infrastructure.rdi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class NutrientAuthorityStore {

  private final Map<
          JsonNutritionAuthority,
          Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      nutrientStore = new EnumMap<>(JsonNutritionAuthority.class);

  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void init() {
    loadNutrients();
    loadOthers();
  }

  private void loadNutrients() {
    for (JsonNutritionAuthority authority : JsonNutritionAuthority.values()) {
      String path = String.format("rdi/%s/nutrients.json", authority.name());
      readAndFill(path, authority);
      log.info("Loaded nutrient requirements for authority: {}", authority.name());
    }
  }

  private void loadOthers() {
    for (JsonNutritionAuthority authority : JsonNutritionAuthority.values()) {
      String path = String.format("rdi/%s/others.json", authority.name());
      readAndFill(path, authority);
      log.info("Loaded other requirements for authority: {}", authority.name());
    }
  }

  private void readAndFill(String path, JsonNutritionAuthority authority) {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
      if (inputStream == null) {
        log.debug("No resource found for: {}", path);
        return;
      }

      JsonNode root = mapper.readTree(inputStream);
      JsonNode nutrientsNode = root.get("nutrients");
      JsonNode sourceNode = root.get("source");

      if (sourceNode != null) {
        log.info("Seeding {} with source: {}", path, sourceNode.asText());
      }

      if (nutrientsNode == null) {
        log.warn("Invalid JSON: missing 'nutrients' node in {}", path);
        return;
      }

      Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
          nutrientRequirements =
              nutrientStore.computeIfAbsent(
                  authority, k -> new EnumMap<>(JsonAllowedNutrients.class));

      Iterator<String> nutrientNames = nutrientsNode.fieldNames();
      while (nutrientNames.hasNext()) {
        String nutrientKey = nutrientNames.next();
        JsonAllowedNutrients nutrient = parseNutrient(nutrientKey, path, authority);
        if (nutrient == null) continue;

        JsonNode nutrientNode = nutrientsNode.get(nutrientKey);

        Iterator<String> groupNames = nutrientNode.fieldNames();
        while (groupNames.hasNext()) {
          String groupKey = groupNames.next();

          if ("note".equalsIgnoreCase(groupKey)) {
            log.info(
                "Skipping root-level note for nutrient {} in authority {}",
                nutrient.name(),
                authority.name());
            continue;
          }

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

            String normalizedUnit = normalizeUnit(unit);
            String normalizedEnumUnit = normalizeUnit(nutrient.getUnit());

            if (!normalizedUnit.equals(normalizedEnumUnit)) {
              log.warn(
                  "Unit mismatch for nutrient {} in authority {} (JSON: {}, Enum: {})",
                  nutrient.name(),
                  authority.name(),
                  unit,
                  nutrient.getUnit());
            }

            boolean isDerived = values.has("isDerived") && values.get("isDerived").asBoolean(false);
            Optional<RdiBasis> basis =
                values.has("basis") && !values.get("basis").isNull()
                    ? parseBasis(values.get("basis").asText())
                    : Optional.empty();

            Optional<Double> divisor =
                values.has("divisor") && !values.get("divisor").isNull()
                    ? Optional.of(values.get("divisor").asDouble())
                    : Optional.empty();

            Optional<String> note =
                values.has("note") && !values.get("note").isNull()
                    ? Optional.of(values.get("note").asText())
                    : Optional.empty();

            JsonNutrientRdiRange requirement =
                new JsonNutrientRdiRange(
                    bounds[0], bounds[1], rdiMin, rdiMax, unit, isDerived, basis, divisor, note);

            nutrientRequirements
                .computeIfAbsent(nutrient, k -> new EnumMap<>(JsonPopulationGroup.class))
                .computeIfAbsent(group, k -> new HashSet<>())
                .add(requirement);
          }
        }
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to load nutrient requirements from " + path, e);
    }
  }

  public Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
      getRequirements(NutritionAuthority authority) {
    try {
      JsonNutritionAuthority jsonAuth = JsonNutritionAuthority.valueOf(authority.name());
      return nutrientStore.getOrDefault(jsonAuth, Collections.emptyMap());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("No matching JsonNutritionAuthority for " + authority, e);
    }
  }

  private JsonAllowedNutrients parseNutrient(
      String key, String path, JsonNutritionAuthority authority) {
    try {
      return JsonAllowedNutrients.valueOf(key);
    } catch (IllegalArgumentException e) {
      log.error(
          "Skipping unknown nutrient '{}' in authority {} (file: {})", key, authority.name(), path);
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

  private String normalizeUnit(String unit) {
    if (unit == null) return null;

    String normalized = unit;

    // Normalize mu/micro variants
    normalized = normalized.replace("μ", "µ");

    // Normalize common synonyms
    normalized =
        normalized
            .replaceAll("(?i)grams?", "g")
            .replaceAll("(?i)milligrams?", "mg")
            .replaceAll("(?i)micrograms?", "µg")
            .replaceAll("(?i)liters?", "L")
            .replaceAll("(?i)liter", "L");

    // Trim and lowercase for safe compare
    return normalized.trim().toLowerCase();
  }
}

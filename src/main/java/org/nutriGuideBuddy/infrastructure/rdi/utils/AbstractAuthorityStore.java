package org.nutriGuideBuddy.infrastructure.rdi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.infrastructure.rdi.dto.*;

@Slf4j
public abstract class AbstractAuthorityStore {

  protected final ObjectMapper mapper = new ObjectMapper();

  protected JsonAllowedNutrients parseNutrient(String key, String context) {
    try {
      return JsonAllowedNutrients.valueOf(key);
    } catch (IllegalArgumentException e) {
      log.error("Skipping unknown nutrient {} in {}", key, context);
      return null;
    }
  }

  protected Optional<RdiBasis> parseBasis(String text) {
    try {
      return Optional.of(RdiBasis.valueOf(text));
    } catch (IllegalArgumentException e) {
      log.error("Unknown RdiBasis value: {}", text);
      return Optional.empty();
    }
  }

  protected double[] parseAgeRange(String range) {
    if (range == null || range.isBlank()) return new double[] {0, 0};
    String[] parts = range.split("-");
    if (parts.length == 2) {
      return new double[] {Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }
    throw new IllegalArgumentException("Invalid age range format: " + range);
  }

  protected String normalizeUnit(String unit) {
    if (unit == null) return null;
    String normalized = unit;
    normalized = normalized.replace("μ", "µ");
    normalized =
        normalized
            .replaceAll("(?i)grams?", "g")
            .replaceAll("(?i)milligrams?", "mg")
            .replaceAll("(?i)micrograms?", "µg")
            .replaceAll("(?i)liters?", "L")
            .replaceAll("(?i)liter", "L");
    return normalized.trim().toLowerCase();
  }

  protected boolean validateUnit(JsonAllowedNutrients nutrient, String jsonUnit, String context) {
    String normalizedUnit = normalizeUnit(jsonUnit);
    String normalizedEnumUnit = normalizeUnit(nutrient.getUnit());

    if (!normalizedUnit.equals(normalizedEnumUnit)) {
      log.warn(
          "Unit mismatch for nutrient {} ({}). JSON: {}, Enum: {} -> Skipping",
          nutrient.name(),
          context,
          jsonUnit,
          nutrient.getUnit());
      return false;
    }
    return true;
  }

  protected JsonNutrientRdiRange buildRange(
      JsonNode values, JsonAllowedNutrients nutrient, String context) {

    double ageMin = values.has("ageMin") ? values.get("ageMin").asDouble() : 0;
    double ageMax = values.has("ageMax") ? values.get("ageMax").asDouble() : 0;

    Optional<Double> rdiMin =
        values.has("rdiMin") && !values.get("rdiMin").isNull()
            ? Optional.of(values.get("rdiMin").asDouble())
            : Optional.empty();
    Optional<Double> rdiMax =
        values.has("rdiMax") && !values.get("rdiMax").isNull()
            ? Optional.of(values.get("rdiMax").asDouble())
            : Optional.empty();

    if (rdiMin.isPresent() && rdiMax.isPresent() && rdiMin.get() > rdiMax.get()) {
      log.warn(
          "Skipping nutrient {} in {} because rdiMin={} is not less than rdiMax={}. Entry: {}",
          nutrient.name(),
          context,
          rdiMin.get(),
          rdiMax.get(),
          values);
      return null;
    }

    String unit =
        values.has("unit") && !values.get("unit").isNull()
            ? values.get("unit").asText()
            : nutrient.getUnit();

    if (!validateUnit(nutrient, unit, context)) {
      log.warn(
          "Skipping nutrient {} in {} because of unit mismatch. JSON unit: {}, Expected: {}. Entry: {}",
          nutrient.name(),
          context,
          unit,
          nutrient.getUnit(),
          values);
      return null;
    }

    if (ageMin > ageMax) {
      log.warn(
          "Skipping nutrient {} in {} because of invalid age range (ageMin={} > ageMax={}). Entry: {}",
          nutrient.name(),
          context,
          ageMin,
          ageMax,
          values);
      return null;
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

    return new JsonNutrientRdiRange(
        ageMin, ageMax, rdiMin, rdiMax, unit, isDerived, basis, divisor, note);
  }
}

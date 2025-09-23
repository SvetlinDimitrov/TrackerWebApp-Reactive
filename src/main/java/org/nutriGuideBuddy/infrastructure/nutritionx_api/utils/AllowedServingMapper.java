package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

public final class AllowedServingMapper {

  private AllowedServingMapper() {}

  public static Set<ServingCreateRequest> map(FoodItemResponse dto) {
    Set<ServingCreateRequest> out = new LinkedHashSet<>();

    boolean mainSet = false;

    // 1) MAIN: prefer the canonical gram-based primary
    Double g = dto.servingWeightGrams();
    if (isPos(g)) {
      mainSet = addUnique(out, grams(g), true);
    }

    // 2) If no main yet, try Nutritionix metric qty/uom (e.g., 240 ml for beverages)
    if (!mainSet && isPos(dto.nfMetricQty()) && dto.nfMetricUom() != null) {
      ServingMetric metric = toMetric(dto.nfMetricUom());
      if (metric != null) {
        mainSet = addUnique(out, new ServingCreateRequest(dto.nfMetricQty(), metric, true), true);
      }
    }

    // 3) If still no main, use serving_qty + serving_unit if we can map the unit
    if (!mainSet && isPos(dto.servingQty()) && dto.servingUnit() != null) {
      ServingMetric metric = toMetric(dto.servingUnit());
      if (metric != null) {
        mainSet = addUnique(out, new ServingCreateRequest(dto.servingQty(), metric, true), true);
      }
    }

    // 4) Add a 100 g convenience serving (if we know grams & it's not the same as main)
    if (isPos(g)) {
      addUnique(out, grams(100.0), false);
    }

    // 5) Alt measures
    if (dto.measures() != null) {
      dto.measures()
          .forEach(
              m -> {
                Double weightG = m.servingWeight();
                String measure = m.measure();
                Double qty = m.qty();

                if (measure == null) return;

                ServingMetric metric = toMetric(measure);
                if (metric == null) return;

                switch (metric) {
                  case GRAM -> {
                    if (isPos(weightG)) addUnique(out, grams(weightG), false);
                  }
                  case OUNCE -> {
                    if (isPos(weightG)) {
                      double oz = weightG / 28.349523125;
                      addUnique(
                          out,
                          new ServingCreateRequest(round(oz), ServingMetric.OUNCE, false),
                          false);
                    }
                  }
                  case MILLILITER, LITER -> {
                    // Without density, we can only use qty if present
                    if (isPos(qty))
                      addUnique(out, new ServingCreateRequest(qty, metric, false), false);
                  }
                  case CUP, TABLESPOON, TEASPOON, SLICE, PIECE, POUND, KILOGRAM, MILLIGRAM -> {
                    if (isPos(qty))
                      addUnique(out, new ServingCreateRequest(qty, metric, false), false);
                  }
                }
              });
    }

    // 6) As a last resort, if NOTHING made it in, add a generic 1 piece main
    if (out.isEmpty()) {
      addUnique(out, new ServingCreateRequest(1.0, ServingMetric.PIECE, true), true);
    }

    // ensure OnlyOneMainServing: demote extra mains if any
    enforceSingleMain(out);

    return out;
  }

  // ---------------- helpers ----------------

  private static ServingCreateRequest grams(double amount) {
    return new ServingCreateRequest(round(amount), ServingMetric.GRAM, false);
  }

  private static boolean addUnique(
      Set<ServingCreateRequest> set, ServingCreateRequest s, boolean asMain) {
    ServingCreateRequest toAdd =
        asMain
            ? new ServingCreateRequest(s.amount(), s.metric(), true)
            : new ServingCreateRequest(s.amount(), s.metric(), false);
    return set.add(toAdd);
  }

  private static void enforceSingleMain(Set<ServingCreateRequest> set) {
    boolean foundMain = false;
    Set<ServingCreateRequest> fixed = new LinkedHashSet<>();
    for (ServingCreateRequest s : set) {
      if (!foundMain && Boolean.TRUE.equals(s.main())) {
        fixed.add(s);
        foundMain = true;
      } else {
        fixed.add(new ServingCreateRequest(s.amount(), s.metric(), false));
      }
    }
    set.clear();
    set.addAll(fixed);
  }

  private static boolean isPos(Double d) {
    return d != null && d > 0.0;
  }

  private static double round(Double d) {
    return Math.round(d * 1000.0) / 1000.0;
  }

  /**
   * Very tolerant mapping from Nutritionix/label strings to ServingMetric. Handles plurals and
   * common synonyms.
   */
  private static ServingMetric toMetric(String raw) {
    if (raw == null) return null;
    String s = raw.trim().toLowerCase(Locale.ROOT);

    // grams
    if (equalsAny(s, "g", "gram", "grams")) return ServingMetric.GRAM;
    if (equalsAny(s, "kg", "kilogram", "kilograms")) return ServingMetric.KILOGRAM;
    if (equalsAny(s, "mg", "milligram", "milligrams")) return ServingMetric.MILLIGRAM;

    // volume
    if (equalsAny(s, "l", "liter", "litre", "liters", "litres")) return ServingMetric.LITER;
    if (equalsAny(s, "ml", "milliliter", "millilitre", "milliliters", "millilitres"))
      return ServingMetric.MILLILITER;

    // cup & spoon variants (be strict before checking for “slice”)
    if (s.startsWith("cup"))
      return ServingMetric.CUP; // "cup", "cups", "cup slices", "cup, quartered..."
    if (equalsAny(s, "cup", "cups")) return ServingMetric.CUP;
    if (equalsAny(s, "tbsp", "tablespoon", "tablespoons")) return ServingMetric.TABLESPOON;
    if (equalsAny(s, "tsp", "teaspoon", "teaspoons")) return ServingMetric.TEASPOON;

    // mass (imperial)
    if (equalsAny(s, "oz", "ounce", "ounces")) return ServingMetric.OUNCE;
    if (equalsAny(s, "lb", "pound", "pounds")) return ServingMetric.POUND;

    // piece/slice — only when the token itself is slice(s) or piece(s)
    if (equalsAny(s, "slice", "slices")) return ServingMetric.SLICE;
    if (equalsAny(s, "piece", "pieces", "pc", "pcs", "serving")) return ServingMetric.PIECE;

    // unknown → not mapped
    return null;
  }

  private static boolean equalsAny(String s, String... options) {
    for (String o : options) {
      if (Objects.equals(s, o)) return true;
    }
    return false;
  }
}

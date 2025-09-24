package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

public final class AllowedServingMapper {

  private AllowedServingMapper() {}

  public static Set<ServingCreateRequest> map(FoodItemResponse dto) {
    Set<ServingCreateRequest> out = new LinkedHashSet<>();
    boolean mainSet = false;

    // 1) MAIN: prefer canonical gram-based primary from Nutritionix
    Double g = dto.servingWeightGrams();
    if (isPos(g)) {
      out.add(new ServingCreateRequest(true, "gram", round(g), round(g)));
      mainSet = true;
    }

    // 2) If no main yet, try nf_metric_qty + nf_metric_uom (use proportional grams if we know g)
    if (!mainSet && isPos(dto.nfMetricQty()) && notBlank(dto.nfMetricUom())) {
      String metric = dto.nfMetricUom().trim();
      double amount = round(dto.nfMetricQty());
      if (isPos(g)) {
        double gramsTotal = round(g / dto.nfMetricQty() * amount);
        if (gramsTotal >= 0.1) {
          out.add(new ServingCreateRequest(true, metric, amount, gramsTotal));
          mainSet = true;
        }
      }
    }

    // 3) Still no main? Try serving_qty + serving_unit (proportional to g)
    if (!mainSet && isPos(dto.servingQty()) && notBlank(dto.servingUnit())) {
      String metric = dto.servingUnit().trim();
      double amount = round(dto.servingQty());
      if (isPos(g)) {
        double gramsTotal = round(g / dto.servingQty() * amount);
        if (gramsTotal >= 0.1) {
          out.add(new ServingCreateRequest(true, metric, amount, gramsTotal));
          mainSet = true;
        }
      }
    }

    // 4) Convenience 100 g serving if we know grams
    if (isPos(g)) {
      out.add(new ServingCreateRequest(false, "gram", 100.0, 100.0));
    }

    // 5) Alt measures: Nutritionix gives serving_weight (grams) for the given qty+measure
    if (dto.measures() != null) {
      dto.measures()
          .forEach(
              m -> {
                String measure = m.measure();
                Double qty = m.qty();
                Double weightG = m.servingWeight();
                if (!notBlank(measure) || !isPos(qty) || !isPos(weightG)) return;

                String metric = measure.trim();
                double amount = round(qty);
                double gramsTotal = round(weightG); // already for that qty+measure
                if (gramsTotal >= 0.1) {
                  out.add(new ServingCreateRequest(false, metric, amount, gramsTotal));
                }
              });
    }

    // 6) Last resort: ensure at least one valid main to satisfy DTO validation
    if (out.isEmpty()) {
      // Use Nutritionix's serving_unit if available; assume 1 unit ≈ 1 g as a minimal placeholder
      String metric = notBlank(dto.servingUnit()) ? dto.servingUnit().trim() : "piece";
      out.add(new ServingCreateRequest(true, metric, 1.0, 1.0));
    }

    // Ensure only one main
    enforceSingleMain(out);

    return out;
  }

  // --------------- helpers ---------------

  private static boolean notBlank(String s) {
    return s != null && !s.trim().isEmpty();
  }

  private static boolean isPos(Double d) {
    return d != null && d > 0.0;
  }

  private static double round(Double d) {
    return Math.round(d * 1000.0) / 1000.0;
  }

  private static void enforceSingleMain(Set<ServingCreateRequest> set) {
    boolean foundMain = false;
    Set<ServingCreateRequest> fixed = new LinkedHashSet<>();
    for (ServingCreateRequest s : set) {
      if (!foundMain && Boolean.TRUE.equals(s.main())) {
        fixed.add(s);
        foundMain = true;
      } else {
        fixed.add(new ServingCreateRequest(false, s.metric(), s.amount(), s.gramsTotal()));
      }
    }
    set.clear();
    set.addAll(fixed);
  }
}

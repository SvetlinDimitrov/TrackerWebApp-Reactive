package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

public final class AllowedNutrientMapper {

  private AllowedNutrientMapper() {}

  /** Map Nutritionix nutrients → your AllowedNutrients set (units normalized). */
  public static Set<NutritionCreateRequest> map(FoodItemResponse dto) {
    Set<NutritionCreateRequest> out = new LinkedHashSet<>();

    Map<Integer, BigDecimal> full =
        dto.fullNutrients() == null
            ? Map.of()
            : dto.fullNutrients().stream()
                .filter(fn -> fn.tag() != null && fn.value() != null)
                .collect(
                    Collectors.toMap(
                        FoodItemResponse.FullNutrientResponse::tag,
                        FoodItemResponse.FullNutrientResponse::value,
                        BigDecimal::add,
                        LinkedHashMap::new));

    Function<Integer, Double> byId =
        id -> {
          BigDecimal v = full.get(id);
          return v == null ? null : v.doubleValue();
        };

    // ---------- MACROS ----------
    put(out, AllowedNutrients.CARBOHYDRATE, coalesce(dto.nfTotalCarbohydrate(), byId.apply(205)));
    put(out, AllowedNutrients.PROTEIN, coalesce(dto.nfProtein(), byId.apply(203)));
    put(out, AllowedNutrients.FAT, coalesce(dto.nfTotalFat(), byId.apply(204)));
    put(out, AllowedNutrients.FIBER, coalesce(dto.nfDietaryFiber(), byId.apply(291)));
    put(out, AllowedNutrients.SUGAR, coalesce(dto.nfSugars(), byId.apply(269)));

    // Saturated / Trans
    put(out, AllowedNutrients.SATURATED_FAT, byId.apply(606));
    put(out, AllowedNutrients.TRANS_FAT, byId.apply(605));

    // Omega-6 / Omega-3
    put(out, AllowedNutrients.OMEGA6, byId.apply(675));
    put(out, AllowedNutrients.OMEGA3, byId.apply(851));

    // Water g → L
    putWaterGramsToLiters(out, AllowedNutrients.WATER, byId.apply(255));

    // Cholesterol
    put(out, AllowedNutrients.CHOLESTEROL, coalesce(dto.nfCholesterol(), byId.apply(601)));

    // ---------- VITAMINS ----------
    put(out, AllowedNutrients.VITAMIN_A, byId.apply(320));
    put(out, AllowedNutrients.VITAMIN_D_D2_D3, byId.apply(328));
    put(out, AllowedNutrients.VITAMIN_E, byId.apply(323));
    put(out, AllowedNutrients.VITAMIN_K, byId.apply(430));
    put(out, AllowedNutrients.VITAMIN_C, byId.apply(401));
    put(out, AllowedNutrients.VITAMIN_B1_THIAMIN, byId.apply(404));
    put(out, AllowedNutrients.VITAMIN_B2_RIBOFLAVIN, byId.apply(405));
    put(out, AllowedNutrients.VITAMIN_B3_NIACIN, byId.apply(406));
    put(out, AllowedNutrients.VITAMIN_B5_PANTOTHENIC_ACID, byId.apply(410));
    put(out, AllowedNutrients.VITAMIN_B6, byId.apply(415));
    put(out, AllowedNutrients.VITAMIN_B7_BIOTIN, byId.apply(416));
    put(out, AllowedNutrients.VITAMIN_B9_FOLATE, coalesce(byId.apply(435), byId.apply(417)));
    put(out, AllowedNutrients.VITAMIN_B12, byId.apply(418));
    put(out, AllowedNutrients.CHOLINE, byId.apply(421));

    // ---------- MINERALS ----------
    put(out, AllowedNutrients.CALCIUM_CA, byId.apply(301));
    put(out, AllowedNutrients.PHOSPHORUS_P, byId.apply(305));
    putUgToMg(out, AllowedNutrients.FLUORIDE, byId.apply(313)); // µg → mg
    // put(out, AllowedNutrients.CHLORIDE, byId.apply(<CHLORIDE_ATTR_ID>));
    put(out, AllowedNutrients.MAGNESIUM_MG, byId.apply(304));
    put(out, AllowedNutrients.SODIUM_NA, coalesce(dto.nfSodium(), byId.apply(307)));
    put(out, AllowedNutrients.POTASSIUM_K, coalesce(dto.nfPotassium(), byId.apply(306)));
    put(out, AllowedNutrients.IRON_FE, byId.apply(303));
    put(out, AllowedNutrients.ZINC_ZN, byId.apply(309));
    putMgToUg(out, AllowedNutrients.COPPER_CU, byId.apply(312)); // mg → µg
    put(out, AllowedNutrients.MANGANESE_MN, byId.apply(315));
    // put(out, AllowedNutrients.IODINE_I, byId.apply(<IODINE_ATTR_ID>));
    put(out, AllowedNutrients.SELENIUM_SE, byId.apply(317));
    // put(out, AllowedNutrients.CHROMIUM_CR,   byId.apply(<CHROMIUM_ATTR_ID>));
    // put(out, AllowedNutrients.MOLYBDENUM_MO, byId.apply(<MOLYBDENUM_ATTR_ID>));

    return out;
  }

  // ===== helpers: keep only strictly positive values (> 0) ===================

  private static void put(Set<NutritionCreateRequest> out, AllowedNutrients n, Double amount) {
    if (isPositive(amount)) {
      out.add(new NutritionCreateRequest(n.getNutrientName(), n.getNutrientUnit(), round(amount)));
    }
  }

  private static void putMgToUg(Set<NutritionCreateRequest> out, AllowedNutrients n, Double mg) {
    if (!isPositive(mg)) return;
    double ug = mg * 1000.0;
    if (isPositive(ug)) {
      out.add(new NutritionCreateRequest(n.getNutrientName(), n.getNutrientUnit(), round(ug)));
    }
  }

  private static void putUgToMg(Set<NutritionCreateRequest> out, AllowedNutrients n, Double ug) {
    if (!isPositive(ug)) return;
    double mg = ug / 1000.0;
    if (isPositive(mg)) {
      out.add(new NutritionCreateRequest(n.getNutrientName(), n.getNutrientUnit(), round(mg)));
    }
  }

  private static void putWaterGramsToLiters(
      Set<NutritionCreateRequest> out, AllowedNutrients n, Double g) {
    if (!isPositive(g)) return;
    double liters = g / 1000.0;
    if (isPositive(liters)) {
      out.add(new NutritionCreateRequest(n.getNutrientName(), n.getNutrientUnit(), round(liters)));
    }
  }

  private static boolean isPositive(Double v) {
    return v != null && v > 0.0;
  }

  private static Double coalesce(Double a, Double b) {
    return a != null ? a : b;
  }

  private static double round(Double d) {
    return Math.round(d * 1000.0) / 1000.0;
  }
}

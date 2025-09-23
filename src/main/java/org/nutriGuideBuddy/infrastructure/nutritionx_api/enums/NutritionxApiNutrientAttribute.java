package org.nutriGuideBuddy.infrastructure.nutritionx_api.enums;

import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Nutrient attributes (reordered: macros vs micronutrients, with minerals/vitamins split).
 *
 * <p>
 *
 * <p>Fields: - id: attr_id - nfp2018: whether it appears on 2018 NFP (true/false from your
 * 1/0/blank) - usdaTag: USDA/INFOODS-style tag (nullable if not provided / "NULL") - label:
 * human-readable name (as provided) - unit: canonical unit (as provided)
 *
 * <p>Lookups: - byId(int) - byUsdaTag(String) — case-insensitive; returns empty if tag is null -
 * sortedById()
 */
@Getter
@RequiredArgsConstructor
public enum NutritionxApiNutrientAttribute {

  // =========================
  // MACROS & ENERGY
  // =========================
  ENERC_KCAL(208, true, "ENERC_KCAL", "Energy", "kcal"),
  ENERC_KJ(268, false, "ENERC_KJ", "Energy", "kJ"),

  PROCNT(203, true, "PROCNT", "Protein", "g"),
  ADJUSTED_PROTEIN(257, false, null, "Adjusted Protein", "g"),

  FAT(204, true, "FAT", "Total lipid (fat)", "g"),
  FASAT(606, true, "FASAT", "Fatty acids, total saturated", "g"),
  FAMS(645, false, "FAMS", "Fatty acids, total monounsaturated", "g"),
  FAPU(646, false, "FAPU", "Fatty acids, total polyunsaturated", "g"),
  FATRN(605, true, "FATRN", "Fatty acids, total trans", "g"),
  FATRNM(693, false, "FATRNM", "Fatty acids, total trans-monoenoic", "g"),
  FATRNP(695, false, "FATRNP", "Fatty acids, total trans-polyenoic", "g"),

  CHOCDF(205, true, "CHOCDF", "Carbohydrate, by difference", "g"),
  STARCH(209, false, "STARCH", "Starch", "g"),
  SUGAR(269, true, "SUGAR", "Sugars, total", "g"),
  SUGAR_ADD(539, true, "SUGAR_ADD", "Sugars, added", "g"),
  GLUS(211, false, "GLUS", "Glucose (dextrose)", "g"),
  FRUS(212, false, "FRUS", "Fructose", "g"),
  SUCS(210, false, "SUCS", "Sucrose", "g"),
  MALS(214, false, "MALS", "Maltose", "g"),
  LACS(213, false, "LACS", "Lactose", "g"),
  GALS(287, false, "GALS", "Galactose", "g"),

  FIBTG(291, true, "FIBTG", "Fiber, total dietary", "g"),
  ALC(221, false, "ALC", "Alcohol, ethyl", "g"),
  WATER(255, false, "WATER", "Water", "g"),
  ASH(207, false, "ASH", "Ash", "g"),

  // Amino acids (subgroup of protein)
  ALA_G(513, false, "ALA_G", "Alanine", "g"),
  ARG_G(511, false, "ARG_G", "Arginine", "g"),
  ASP_G(514, false, "ASP_G", "Aspartic acid", "g"),
  CYS_G(507, false, "CYS_G", "Cystine", "g"),
  GLU_G(515, false, "GLU_G", "Glutamic acid", "g"),
  GLY_G(516, false, "GLY_G", "Glycine", "g"),
  HISTN_G(512, false, "HISTN_G", "Histidine", "g"),
  ILE_G(503, false, "ILE_G", "Isoleucine", "g"),
  LEU_G(504, false, "LEU_G", "Leucine", "g"),
  LYS_G(505, false, "LYS_G", "Lysine", "g"),
  MET_G(506, false, "MET_G", "Methionine", "g"),
  PHE_G(508, false, "PHE_G", "Phenylalanine", "g"),
  PRO_G(517, false, "PRO_G", "Proline", "g"),
  SER_G(518, false, "SER_G", "Serine", "g"),
  THR_G(502, false, "THR_G", "Threonine", "g"),
  TRP_G(501, false, "TRP_G", "Tryptophan", "g"),
  TYR_G(509, false, "TYR_G", "Tyrosine", "g"),
  VAL_G(510, false, "VAL_G", "Valine", "g"),
  HYP(521, false, "HYP", "Hydroxyproline", "g"),

  // =========================
  // MICRONUTRIENTS
  //   Minerals
  // =========================
  CA(301, true, "CA", "Calcium, Ca", "mg"),
  FE(303, true, "FE", "Iron, Fe", "mg"),
  MG(304, false, "MG", "Magnesium, Mg", "mg"),
  P(305, false, "P", "Phosphorus, P", "mg"),
  K(306, true, "K", "Potassium, K", "mg"),
  NA(307, true, "NA", "Sodium, Na", "mg"),
  ZN(309, false, "ZN", "Zinc, Zn", "mg"),
  CU(312, false, "CU", "Copper, Cu", "mg"),
  MN(315, false, "MN", "Manganese, Mn", "mg"),
  SE(317, false, "SE", "Selenium, Se", "µg"),
  FLD(313, false, "FLD", "Fluoride, F", "µg"),

  // =========================
  // MICRONUTRIENTS
  //   Vitamins (A/D/E/K families, B-complex, C)
  // =========================
  // Vitamin A family & carotenoids
  VITA_RAE(320, false, "VITA_RAE", "Vitamin A, RAE", "µg"),
  VITA_IU(318, false, "VITA_IU", "Vitamin A, IU", "IU"),
  RETOL(319, false, "RETOL", "Retinol", "µg"),
  CARTA(322, false, "CARTA", "Carotene, alpha", "µg"),
  CARTB(321, false, "CARTB", "Carotene, beta", "µg"),
  CRYPX(334, false, "CRYPX", "Cryptoxanthin, beta", "µg"),
  LUT_ZEA(338, false, "LUT+ZEA", "Lutein + zeaxanthin", "µg"),

  // Vitamin D family
  VITD_IU(324, true, "VITD", "Vitamin D", "IU"),
  VITD_MCG(328, false, "VITD", "Vitamin D (D2 + D3)", "µg"),
  CHOCAL(326, false, "CHOCAL", "Vitamin D3 (cholecalciferol)", "µg"),
  ERGCAL(325, false, "ERGCAL", "Vitamin D2 (ergocalciferol)", "µg"),

  // Vitamin E family (tocopherols / tocotrienols)
  TOCPHA(323, false, "TOCPHA", "Vitamin E (alpha-tocopherol)", "mg"),
  TOCPHB(341, false, "TOCPHB", "Tocopherol, beta", "mg"),
  TOCPHG(342, false, "TOCPHG", "Tocopherol, gamma", "mg"),
  TOCPHD(343, false, "TOCPHD", "Tocopherol, delta", "mg"),
  TOCTRA(344, false, "TOCTRA", "Tocotrienol, alpha", "mg"),
  TOCTRB(345, false, "TOCTRB", "Tocotrienol, beta", "mg"),
  TOCTRG(346, false, "TOCTRG", "Tocotrienol, gamma", "mg"),
  TOCTRD(347, false, "TOCTRD", "Tocotrienol,delta", "mg"),
  VITAMIN_E_ADDED(573, false, null, "Vitamin E, added", "mg"),

  // Vitamin K
  VITK1(430, false, "VITK1", "Vitamin K (phylloquinone)", "µg"),
  VITK1D(429, false, "VITK1D", "Dihydrophylloquinone", "µg"),
  MK4(428, false, "MK4", "Menaquinone-4", "µg"),

  // B-complex & choline
  THIA(404, false, "THIA", "Thiamin", "mg"),
  RIBF(405, false, "RIBF", "Riboflavin", "mg"),
  NIA(406, false, "NIA", "Niacin", "mg"),
  PANTAC(410, false, "PANTAC", "Pantothenic acid", "mg"),
  VITB6A(415, false, "VITB6A", "Vitamin B-6", "mg"),
  FOL(417, false, "FOL", "Folate, total", "µg"),
  FOLAC(431, false, "FOLAC", "Folic acid", "µg"),
  FOLFD(432, false, "FOLFD", "Folate, food", "µg"),
  FOLDFE(435, false, "FOLDFE", "Folate, DFE", "µg"),
  VITB12(418, false, "VITB12", "Vitamin B-12", "µg"),
  VITAMIN_B12_ADDED(578, false, null, "Vitamin B-12, added", "µg"),
  CHOLN(421, false, "CHOLN", "Choline, total", "mg"),

  // Vitamin C
  VITC(401, false, "VITC", "Vitamin C, total ascorbic acid", "mg"),

  // =========================
  // LIPIDS — Fatty acid totals already above.
  // INDIVIDUAL FATTY ACID SPECIES
  // =========================
  F4D0(607, false, "F4D0", "04:00", "g"),
  F6D0(608, false, "F6D0", "06:00", "g"),
  F8D0(609, false, "F8D0", "08:00", "g"),
  F10D0(610, false, "F10D0", "10:00", "g"),
  F12D0(611, false, "F12D0", "12:00", "g"),
  F13D0(696, false, "F13D0", "13:00", "g"),
  F14D0(612, false, "F14D0", "14:00", "g"),
  F14D1(625, false, "F14D1", "14:01", "g"),
  F15D0(652, false, "F15D0", "15:00", "g"),
  F15D1(697, false, "F15D1", "15:01", "g"),
  F16D0(613, false, "F16D0", "16:00", "g"),
  F16D1(626, false, "F16D1", "16:1 undifferentiated", "g"),
  F16D1C(673, false, "F16D1C", "16:1 c", "g"),
  F16D1T(662, false, "F16D1T", "16:1 t", "g"),
  F17D0(653, false, "F17D0", "17:00", "g"),
  F17D1(687, false, "F17D1", "17:01", "g"),
  F18D0(614, false, "F18D0", "18:00", "g"),
  F18D1(617, false, "F18D1", "18:1 undifferentiated", "g"),
  F18D1C(674, false, "F18D1C", "18:1 c", "g"),
  F18D1T(663, false, "F18D1T", "18:1 t", "g"),
  F18D1TN7(859, false, "F18D1TN7", "18:1-11t (18:1t n-7)", "g"),
  F18D2(618, false, "F18D2", "18:2 undifferentiated", "g"),
  F18D2CLA(670, false, "F18D2CLA", "18:2 CLAs", "g"),
  F18D2CN6(675, false, "F18D2CN6", "18:2 n-6 c,c", "g"),
  F18D2TT(669, false, "F18D2TT", "18:2 t,t", "g"),
  F18D3(619, false, "F18D3", "18:3 undifferentiated", "g"),
  F18D3CN3(851, false, "F18D3CN3", "18:3 n-3 c,c,c (ALA)", "g"),
  F18D3CN6(685, false, "F18D3CN6", "18:3 n-6 c,c,c", "g"),
  F18D4(627, false, "F18D4", "18:04", "g"),
  F20D0(615, false, "F20D0", "20:00", "g"),
  F20D1(628, false, "F20D1", "20:01", "g"),
  F20D2CN6(672, false, "F20D2CN6", "20:2 n-6 c,c", "g"),
  F20D3(689, false, "F20D3", "20:3 undifferentiated", "g"),
  F20D3N3(852, false, "F20D3N3", "20:3 n-3", "g"),
  F20D3N6(853, false, "F20D3N6", "20:3 n-6", "g"),
  F20D4(620, false, "F20D4", "20:4 undifferentiated", "g"),
  F20D4N6(855, false, "F20D4N6", "20:4 n-6", "g"),
  F20D5(629, false, "F20D5", "20:5 n-3 (EPA)", "g"),
  F21D5(857, false, "F21D5", "21:05", "g"),
  F22D0(624, false, "F22D0", "22:00", "g"),
  F22D1(630, false, "F22D1", "22:1 undifferentiated", "g"),
  F22D4(858, false, "F22D4", "22:04", "g"),
  F22D5(631, false, "F22D5", "22:5 n-3 (DPA)", "g"),
  F22D6(621, false, "F22D6", "22:6 n-3 (DHA)", "g"),
  F24D0(654, false, "F24D0", "24:00:00", "g"),
  F24D1C(671, false, "F24D1C", "24:1 c", "g"),
  FA_22_1_T(664, false, null, "22:1 t", "g"),
  FA_22_1_C(676, false, null, "22:1 c", "g"),
  FA_18_3I(856, false, null, "18:3i", "g"),
  FA_18_2_T_UNSPEC(665, false, null, "18:2 t not further defined", "g"),
  FA_18_2I(666, false, null, "18:2 i", "g"),

  // =========================
  // STEROLS & RELATED
  // =========================
  PHYSTR(636, false, "PHYSTR", "Phytosterols", "mg"),
  CAMD5(639, false, "CAMD5", "Campesterol", "mg"),
  SITSTR(641, false, "SITSTR", "Beta-sitosterol", "mg"),
  STID7(638, false, "STID7", "Stigmasterol", "mg"),
  CHOLE(601, true, "CHOLE", "Cholesterol", "mg"),

  // =========================
  // OTHER BIOACTIVES
  // =========================
  BETN(454, false, "BETN", "Betaine", "mg"),
  CAFFN(262, false, "CAFFN", "Caffeine", "mg"),
  THEBRN(263, false, "THEBRN", "Theobromine", "mg");

  // ----------------------------------------------------------------

  private final int id;
  private final boolean nfp2018;
  private final String usdaTag; // may be null
  private final String label;
  private final String unit;

  private static final Map<Integer, NutritionxApiNutrientAttribute> BY_ID;
  private static final Map<String, NutritionxApiNutrientAttribute> BY_USDA;

  static {
    Map<Integer, NutritionxApiNutrientAttribute> byId = new HashMap<>();
    Map<String, NutritionxApiNutrientAttribute> byUsda = new HashMap<>();
    for (NutritionxApiNutrientAttribute n : values()) {
      byId.put(n.id, n);
      if (n.usdaTag != null && !n.usdaTag.isEmpty()) {
        byUsda.put(n.usdaTag.toUpperCase(Locale.ROOT), n);
      }
    }
    BY_ID = Collections.unmodifiableMap(byId);
    BY_USDA = Collections.unmodifiableMap(byUsda);
  }

  public static Optional<NutritionxApiNutrientAttribute> byId(int id) {
    return Optional.ofNullable(BY_ID.get(id));
  }

  public static Optional<NutritionxApiNutrientAttribute> byUsdaTag(String tag) {
    if (tag == null) return Optional.empty();
    return Optional.ofNullable(BY_USDA.get(tag.toUpperCase(Locale.ROOT)));
  }
}

package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.types;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.RdiProvider;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.RdiRange;
import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Standard Recommended Daily Intake (RDI) data.
 *
 * <p>Includes macronutrients, minerals, and vitamins. Based on NIH sources: - Macronutrients: <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK56068/">...</a> - Minerals: <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK545442/table/appJ_tab3/">...</a> - Vitamins: <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t2/">...</a>
 */
public class StandardRdiData implements RdiProvider {

  // === Macronutrients ===

  public List<RdiRange> getFiberRdi() {
    return List.of(
        new RdiRange(1, 3, null, 19),
        new RdiRange(4, 8, null, 25),
        new RdiRange(9, 13, Gender.MALE, 31),
        new RdiRange(9, 18, Gender.FEMALE, 26),
        new RdiRange(14, 50, Gender.MALE, 38),
        new RdiRange(14, 50, Gender.FEMALE, 25),
        new RdiRange(51, 150, Gender.MALE, 30),
        new RdiRange(51, 150, Gender.FEMALE, 21));
  }

  public List<RdiRange> getWaterRdi() {
    return List.of(
        new RdiRange(1, 3, null, 1.3),
        new RdiRange(4, 8, null, 1.7),
        new RdiRange(9, 13, Gender.MALE, 2.4),
        new RdiRange(9, 13, Gender.FEMALE, 2.1),
        new RdiRange(14, 18, Gender.MALE, 3.3),
        new RdiRange(14, 18, Gender.FEMALE, 2.3),
        new RdiRange(19, 150, Gender.MALE, 3.7),
        new RdiRange(19, 150, Gender.FEMALE, 2.7));
  }

  public List<RdiRange> getSugarRdi() {
    return List.of(
        new RdiRange(2, 18, null, 25),
        new RdiRange(19, 150, Gender.FEMALE, 25),
        new RdiRange(19, 150, Gender.MALE, 36));
  }

  public List<RdiRange> getCholesterolRdi() {
    return List.of(new RdiRange(0, 150, null, 300));
  }

  public List<RdiRange> getSaturatedFatRdi() {
    return List.of(new RdiRange(0, 150, null, 0));
  }

  public List<RdiRange> getTransFatRdi() {
    return List.of(new RdiRange(0, 150, null, 0));
  }

  // === Minerals ===

  public List<RdiRange> getCalciumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 700),
        new RdiRange(4, 8, null, 1000),
        new RdiRange(9, 18, null, 1300),
        new RdiRange(19, 50, null, 1000),
        new RdiRange(51, 150, null, 1200));
  }

  public List<RdiRange> getChromiumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 11),
        new RdiRange(4, 8, null, 15),
        new RdiRange(9, 13, Gender.MALE, 25),
        new RdiRange(9, 13, Gender.FEMALE, 21),
        new RdiRange(14, 50, Gender.MALE, 35),
        new RdiRange(14, 18, Gender.FEMALE, 24),
        new RdiRange(19, 50, Gender.FEMALE, 25),
        new RdiRange(51, 150, Gender.MALE, 30),
        new RdiRange(51, 150, Gender.FEMALE, 20));
  }

  public List<RdiRange> getCopperRdi() {
    return List.of(
        new RdiRange(1, 3, null, 340),
        new RdiRange(4, 8, null, 440),
        new RdiRange(9, 13, null, 700),
        new RdiRange(14, 18, null, 890),
        new RdiRange(19, 150, null, 900));
  }

  public List<RdiRange> getFluorideRdi() {
    return List.of(
        new RdiRange(1, 3, null, 0.7),
        new RdiRange(4, 8, null, 1.0),
        new RdiRange(9, 13, null, 2.0),
        new RdiRange(14, 18, Gender.MALE, 3.0),
        new RdiRange(14, 18, Gender.FEMALE, 2.0),
        new RdiRange(19, 150, Gender.MALE, 4.0),
        new RdiRange(19, 150, Gender.FEMALE, 3.0));
  }

  public List<RdiRange> getIodineRdi() {
    return List.of(
        new RdiRange(1, 8, null, 90),
        new RdiRange(9, 13, null, 120),
        new RdiRange(14, 150, null, 150));
  }

  public List<RdiRange> getIronRdi() {
    return List.of(
        new RdiRange(1, 3, null, 7),
        new RdiRange(4, 8, null, 10),
        new RdiRange(9, 13, null, 8),
        new RdiRange(14, 18, Gender.MALE, 11),
        new RdiRange(14, 18, Gender.FEMALE, 15),
        new RdiRange(19, 50, Gender.MALE, 8),
        new RdiRange(19, 50, Gender.FEMALE, 18),
        new RdiRange(51, 150, null, 8));
  }

  public List<RdiRange> getMagnesiumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 80),
        new RdiRange(4, 8, null, 130),
        new RdiRange(9, 13, null, 240),
        new RdiRange(14, 18, Gender.MALE, 410),
        new RdiRange(14, 18, Gender.FEMALE, 360),
        new RdiRange(19, 30, Gender.MALE, 400),
        new RdiRange(19, 30, Gender.FEMALE, 310),
        new RdiRange(31, 150, Gender.MALE, 420),
        new RdiRange(31, 150, Gender.FEMALE, 320));
  }

  public List<RdiRange> getManganeseRdi() {
    return List.of(
        new RdiRange(1, 3, null, 1.2),
        new RdiRange(4, 8, null, 1.5),
        new RdiRange(9, 13, Gender.MALE, 1.9),
        new RdiRange(9, 18, Gender.FEMALE, 1.6),
        new RdiRange(14, 18, Gender.MALE, 2.2),
        new RdiRange(19, 150, Gender.MALE, 2.3),
        new RdiRange(19, 150, Gender.FEMALE, 1.8));
  }

  public List<RdiRange> getMolybdenumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 17),
        new RdiRange(4, 8, null, 22),
        new RdiRange(9, 13, null, 34),
        new RdiRange(14, 18, null, 43),
        new RdiRange(19, 150, null, 45));
  }

  public List<RdiRange> getPhosphorusRdi() {
    return List.of(
        new RdiRange(1, 3, null, 460),
        new RdiRange(4, 8, null, 500),
        new RdiRange(9, 18, null, 1250),
        new RdiRange(19, 150, null, 700));
  }

  public List<RdiRange> getSeleniumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 20),
        new RdiRange(4, 8, null, 30),
        new RdiRange(9, 13, null, 40),
        new RdiRange(14, 150, null, 55));
  }

  public List<RdiRange> getZincRdi() {
    return List.of(
        new RdiRange(1, 3, null, 3),
        new RdiRange(4, 8, null, 5),
        new RdiRange(9, 13, null, 8),
        new RdiRange(14, 18, Gender.MALE, 11),
        new RdiRange(14, 18, Gender.FEMALE, 9),
        new RdiRange(19, 150, Gender.MALE, 11),
        new RdiRange(19, 150, Gender.FEMALE, 8));
  }

  public List<RdiRange> getPotassiumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 2000),
        new RdiRange(4, 8, null, 2300),
        new RdiRange(9, 13, Gender.MALE, 2500),
        new RdiRange(9, 18, Gender.FEMALE, 2300),
        new RdiRange(14, 18, Gender.MALE, 3000),
        new RdiRange(19, 150, Gender.MALE, 3400),
        new RdiRange(19, 150, Gender.FEMALE, 2600));
  }

  public List<RdiRange> getSodiumRdi() {
    return List.of(
        new RdiRange(1, 3, null, 800),
        new RdiRange(4, 8, null, 1000),
        new RdiRange(9, 13, null, 1200),
        new RdiRange(14, 150, null, 1500));
  }

  public List<RdiRange> getChlorideRdi() {
    return List.of(
        new RdiRange(1, 3, null, 1.5),
        new RdiRange(4, 8, null, 1.9),
        new RdiRange(9, 50, null, 2.3),
        new RdiRange(51, 70, null, 2.0),
        new RdiRange(71, 150, null, 1.8));
  }

  // === Vitamins ===

  public List<RdiRange> getVitaminARdi() {
    return List.of(
        new RdiRange(1, 3, null, 300),
        new RdiRange(4, 8, null, 400),
        new RdiRange(9, 13, null, 600),
        new RdiRange(14, 150, Gender.MALE, 900),
        new RdiRange(14, 150, Gender.FEMALE, 700));
  }

  public List<RdiRange> getVitaminCRdi() {
    return List.of(
        new RdiRange(1, 3, null, 15),
        new RdiRange(4, 8, null, 25),
        new RdiRange(9, 13, null, 45),
        new RdiRange(14, 18, Gender.MALE, 75),
        new RdiRange(14, 18, Gender.FEMALE, 65),
        new RdiRange(19, 150, Gender.MALE, 90),
        new RdiRange(19, 150, Gender.FEMALE, 75));
  }

  public List<RdiRange> getVitaminDRdi() {
    return List.of(
        new RdiRange(1, 8, null, 15),
        new RdiRange(9, 70, null, 15),
        new RdiRange(71, 150, null, 20));
  }

  public List<RdiRange> getVitaminERdi() {
    return List.of(
        new RdiRange(1, 3, null, 6),
        new RdiRange(4, 8, null, 7),
        new RdiRange(9, 13, null, 11),
        new RdiRange(14, 150, null, 15));
  }

  public List<RdiRange> getVitaminKRdi() {
    return List.of(
        new RdiRange(1, 3, null, 30),
        new RdiRange(4, 8, null, 55),
        new RdiRange(9, 13, null, 60),
        new RdiRange(14, 18, null, 75),
        new RdiRange(19, 150, Gender.MALE, 120),
        new RdiRange(19, 150, Gender.FEMALE, 90));
  }

  public List<RdiRange> getVitaminB1Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 0.5),
        new RdiRange(4, 8, null, 0.6),
        new RdiRange(9, 13, null, 0.9),
        new RdiRange(14, 150, Gender.MALE, 1.2),
        new RdiRange(14, 18, Gender.FEMALE, 1.0),
        new RdiRange(19, 150, Gender.FEMALE, 1.1));
  }

  public List<RdiRange> getVitaminB2Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 0.5),
        new RdiRange(4, 8, null, 0.6),
        new RdiRange(9, 13, null, 0.9),
        new RdiRange(14, 150, Gender.MALE, 1.3),
        new RdiRange(14, 18, Gender.FEMALE, 1.0),
        new RdiRange(19, 150, Gender.FEMALE, 1.1));
  }

  public List<RdiRange> getVitaminB3Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 6),
        new RdiRange(4, 8, null, 8),
        new RdiRange(9, 13, null, 12),
        new RdiRange(14, 150, Gender.MALE, 16),
        new RdiRange(14, 150, Gender.FEMALE, 14));
  }

  public List<RdiRange> getVitaminB6Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 0.5),
        new RdiRange(4, 8, null, 0.6),
        new RdiRange(9, 13, null, 1.0),
        new RdiRange(14, 50, Gender.MALE, 1.3),
        new RdiRange(14, 18, Gender.FEMALE, 1.2),
        new RdiRange(19, 50, Gender.FEMALE, 1.3),
        new RdiRange(51, 150, Gender.MALE, 1.7),
        new RdiRange(51, 150, Gender.FEMALE, 1.5));
  }

  public List<RdiRange> getVitaminB9Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 150),
        new RdiRange(4, 8, null, 200),
        new RdiRange(9, 13, null, 300),
        new RdiRange(14, 150, null, 400));
  }

  public List<RdiRange> getVitaminB12Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 0.9),
        new RdiRange(4, 8, null, 1.2),
        new RdiRange(9, 13, null, 1.8),
        new RdiRange(14, 150, null, 2.4));
  }

  public List<RdiRange> getVitaminB5Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 2),
        new RdiRange(4, 8, null, 3),
        new RdiRange(9, 13, null, 4),
        new RdiRange(14, 150, null, 5));
  }

  public List<RdiRange> getVitaminB7Rdi() {
    return List.of(
        new RdiRange(1, 3, null, 8),
        new RdiRange(4, 8, null, 12),
        new RdiRange(9, 13, null, 20),
        new RdiRange(14, 18, null, 25),
        new RdiRange(19, 150, null, 30));
  }

  public List<RdiRange> getCholineRdi() {
    return List.of(
        new RdiRange(1, 3, null, 200),
        new RdiRange(4, 8, null, 250),
        new RdiRange(9, 13, Gender.MALE, 375),
        new RdiRange(9, 13, Gender.FEMALE, 375),
        new RdiRange(14, 150, Gender.MALE, 550),
        new RdiRange(14, 18, Gender.FEMALE, 400),
        new RdiRange(19, 150, Gender.FEMALE, 425));
  }

  public Map<AllowedNutrients, List<RdiRange>> getAll() {
    return Map.ofEntries(
        // Macronutrients
        Map.entry(AllowedNutrients.Fiber, getFiberRdi()),
        Map.entry(AllowedNutrients.Water, getWaterRdi()),
        Map.entry(AllowedNutrients.Sugar, getSugarRdi()),
        Map.entry(AllowedNutrients.Cholesterol, getCholesterolRdi()),
        Map.entry(AllowedNutrients.Saturated_Fat, getSaturatedFatRdi()),
        Map.entry(AllowedNutrients.Trans_Fat, getTransFatRdi()),

        // Minerals
        Map.entry(AllowedNutrients.Calcium_Ca, getCalciumRdi()),
        Map.entry(AllowedNutrients.Chromium_Cr, getChromiumRdi()),
        Map.entry(AllowedNutrients.Copper_Cu, getCopperRdi()),
        Map.entry(AllowedNutrients.Fluoride, getFluorideRdi()),
        Map.entry(AllowedNutrients.Iodine_I, getIodineRdi()),
        Map.entry(AllowedNutrients.Iron_Fe, getIronRdi()),
        Map.entry(AllowedNutrients.Magnesium_Mg, getMagnesiumRdi()),
        Map.entry(AllowedNutrients.Manganese_Mn, getManganeseRdi()),
        Map.entry(AllowedNutrients.Molybdenum_Mo, getMolybdenumRdi()),
        Map.entry(AllowedNutrients.Phosphorus_P, getPhosphorusRdi()),
        Map.entry(AllowedNutrients.Selenium_Se, getSeleniumRdi()),
        Map.entry(AllowedNutrients.Zinc_Zn, getZincRdi()),
        Map.entry(AllowedNutrients.Potassium_K, getPotassiumRdi()),
        Map.entry(AllowedNutrients.Sodium_Na, getSodiumRdi()),
        Map.entry(AllowedNutrients.Chloride, getChlorideRdi()),

        // Vitamins
        Map.entry(AllowedNutrients.VitaminA, getVitaminARdi()),
        Map.entry(AllowedNutrients.VitaminC, getVitaminCRdi()),
        Map.entry(AllowedNutrients.VitaminD_D2_D3, getVitaminDRdi()),
        Map.entry(AllowedNutrients.VitaminE, getVitaminERdi()),
        Map.entry(AllowedNutrients.VitaminK, getVitaminKRdi()),
        Map.entry(AllowedNutrients.VitaminB1_Thiamin, getVitaminB1Rdi()),
        Map.entry(AllowedNutrients.VitaminB2_Riboflavin, getVitaminB2Rdi()),
        Map.entry(AllowedNutrients.VitaminB3_Niacin, getVitaminB3Rdi()),
        Map.entry(AllowedNutrients.VitaminB6, getVitaminB6Rdi()),
        Map.entry(AllowedNutrients.VitaminB9_Folate, getVitaminB9Rdi()),
        Map.entry(AllowedNutrients.VitaminB12, getVitaminB12Rdi()),
        Map.entry(AllowedNutrients.VitaminB5_PantothenicAcid, getVitaminB5Rdi()),
        Map.entry(AllowedNutrients.VitaminB7_Biotin, getVitaminB7Rdi()),
        Map.entry(AllowedNutrients.Choline, getCholineRdi()));
  }

  @Override
  public double getRecommended(AllowedNutrients nutrient, Gender gender, int age) {
    return getAll().getOrDefault(nutrient, List.of()).stream()
        .filter(r -> age >= r.minAge() && age <= r.maxAge())
        .filter(r -> r.gender() == null || r.gender() == gender)
        .map(RdiRange::value)
        .findFirst()
        .orElse(0.0);
  }

  @Override
  public Set<AllowedNutrients> getSupportedNutrients() {
    return getAll().keySet();
  }
}

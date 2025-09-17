package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Mineral Recommended Dietary Intake (RDI) data source.
 *
 * <p>Data from: <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK545442/table/appJ_tab3/?report=objectonly">...</a>
 *
 * <p>Provides lookup of recommended daily intake for minerals, based on age and gender.
 */
public class MineralRdiData {
  private static final Map<AllowedNutrients, List<RdiRange>> mineralRules =
      Map.ofEntries(
          Map.entry(
              AllowedNutrients.Calcium_Ca,
              List.of(
                  new RdiRange(1, 3, null, 700),
                  new RdiRange(4, 8, null, 1000),
                  new RdiRange(9, 18, null, 1300),
                  new RdiRange(19, 50, null, 1000),
                  new RdiRange(51, 150, null, 1200))),
          Map.entry(
              AllowedNutrients.Chromium_Cr,
              List.of(
                  new RdiRange(1, 3, null, 11),
                  new RdiRange(4, 8, null, 15),
                  new RdiRange(9, 13, Gender.MALE, 25),
                  new RdiRange(9, 13, Gender.FEMALE, 21),
                  new RdiRange(14, 50, Gender.MALE, 35),
                  new RdiRange(14, 18, Gender.FEMALE, 24),
                  new RdiRange(19, 50, Gender.FEMALE, 25),
                  new RdiRange(51, 150, Gender.MALE, 30),
                  new RdiRange(51, 150, Gender.FEMALE, 20))),
          Map.entry(
              AllowedNutrients.Copper_Cu,
              List.of(
                  new RdiRange(1, 3, null, 340),
                  new RdiRange(4, 8, null, 440),
                  new RdiRange(9, 13, null, 700),
                  new RdiRange(14, 18, null, 890),
                  new RdiRange(19, 150, null, 900))),
          Map.entry(
              AllowedNutrients.Fluoride,
              List.of(
                  new RdiRange(1, 3, null, 0.7),
                  new RdiRange(4, 8, null, 1.0),
                  new RdiRange(9, 13, null, 2.0),
                  new RdiRange(14, 18, Gender.MALE, 3.0),
                  new RdiRange(14, 18, Gender.FEMALE, 2.0),
                  new RdiRange(19, 150, Gender.MALE, 4.0),
                  new RdiRange(19, 150, Gender.FEMALE, 3.0))),
          Map.entry(
              AllowedNutrients.Iodine_I,
              List.of(
                  new RdiRange(1, 8, null, 90),
                  new RdiRange(9, 13, null, 120),
                  new RdiRange(14, 150, null, 150))),
          Map.entry(
              AllowedNutrients.Iron_Fe,
              List.of(
                  new RdiRange(1, 3, null, 7),
                  new RdiRange(4, 8, null, 10),
                  new RdiRange(9, 13, null, 8),
                  new RdiRange(14, 18, Gender.MALE, 11),
                  new RdiRange(14, 18, Gender.FEMALE, 15),
                  new RdiRange(19, 50, Gender.MALE, 8),
                  new RdiRange(19, 50, Gender.FEMALE, 18),
                  new RdiRange(51, 150, null, 8))),
          Map.entry(
              AllowedNutrients.Magnesium_Mg,
              List.of(
                  new RdiRange(1, 3, null, 80),
                  new RdiRange(4, 8, null, 130),
                  new RdiRange(9, 13, null, 240),
                  new RdiRange(14, 18, Gender.MALE, 410),
                  new RdiRange(14, 18, Gender.FEMALE, 360),
                  new RdiRange(19, 30, Gender.MALE, 400),
                  new RdiRange(19, 30, Gender.FEMALE, 310),
                  new RdiRange(31, 150, Gender.MALE, 420),
                  new RdiRange(31, 150, Gender.FEMALE, 320))),
          Map.entry(
              AllowedNutrients.Manganese_Mn,
              List.of(
                  new RdiRange(1, 3, null, 1.2),
                  new RdiRange(4, 8, null, 1.5),
                  new RdiRange(9, 13, Gender.MALE, 1.9),
                  new RdiRange(9, 18, Gender.FEMALE, 1.6),
                  new RdiRange(14, 18, Gender.MALE, 2.2),
                  new RdiRange(19, 150, Gender.MALE, 2.3),
                  new RdiRange(19, 150, Gender.FEMALE, 1.8))),
          Map.entry(
              AllowedNutrients.Molybdenum_Mo,
              List.of(
                  new RdiRange(1, 3, null, 17),
                  new RdiRange(4, 8, null, 22),
                  new RdiRange(9, 13, null, 34),
                  new RdiRange(14, 18, null, 43),
                  new RdiRange(19, 150, null, 45))),
          Map.entry(
              AllowedNutrients.Phosphorus_P,
              List.of(
                  new RdiRange(1, 3, null, 460),
                  new RdiRange(4, 8, null, 500),
                  new RdiRange(9, 18, null, 1250),
                  new RdiRange(19, 150, null, 700))),
          Map.entry(
              AllowedNutrients.Selenium_Se,
              List.of(
                  new RdiRange(1, 3, null, 20),
                  new RdiRange(4, 8, null, 30),
                  new RdiRange(9, 13, null, 40),
                  new RdiRange(14, 150, null, 55))),
          Map.entry(
              AllowedNutrients.Zinc_Zn,
              List.of(
                  new RdiRange(1, 3, null, 3),
                  new RdiRange(4, 8, null, 5),
                  new RdiRange(9, 13, null, 8),
                  new RdiRange(14, 18, Gender.MALE, 11),
                  new RdiRange(14, 18, Gender.FEMALE, 9),
                  new RdiRange(19, 150, Gender.MALE, 11),
                  new RdiRange(19, 150, Gender.FEMALE, 8))),
          Map.entry(
              AllowedNutrients.Potassium_K,
              List.of(
                  new RdiRange(1, 3, null, 2000),
                  new RdiRange(4, 8, null, 2300),
                  new RdiRange(9, 13, Gender.MALE, 2500),
                  new RdiRange(9, 18, Gender.FEMALE, 2300),
                  new RdiRange(14, 18, Gender.MALE, 3000),
                  new RdiRange(19, 150, Gender.MALE, 3400),
                  new RdiRange(19, 150, Gender.FEMALE, 2600))),
          Map.entry(
              AllowedNutrients.Sodium_Na,
              List.of(
                  new RdiRange(1, 3, null, 800),
                  new RdiRange(4, 8, null, 1000),
                  new RdiRange(9, 13, null, 1200),
                  new RdiRange(14, 150, null, 1500))),
          Map.entry(
              AllowedNutrients.Chloride,
              List.of(
                  new RdiRange(1, 3, null, 1.5),
                  new RdiRange(4, 8, null, 1.9),
                  new RdiRange(9, 50, null, 2.3),
                  new RdiRange(51, 70, null, 2.0),
                  new RdiRange(71, 150, null, 1.8))));

  /**
   * Returns the recommended daily intake for the given mineral, gender, and age.
   *
   * @param nutrient mineral nutrient
   * @param gender gender of the user
   * @param age age of the user
   * @return recommended intake in the nutrient’s unit, or 0.0 if no match found
   */
  public static double getRecommended(AllowedNutrients nutrient, Gender gender, int age) {
    return mineralRules.getOrDefault(nutrient, List.of()).stream()
        .filter(r -> age >= r.minAge() && age <= r.maxAge())
        .filter(r -> r.gender() == null || r.gender() == gender)
        .map(RdiRange::value)
        .findFirst()
        .orElse(0.0);
  }

  public static Set<AllowedNutrients> getSupportedNutrients() {
    return mineralRules.keySet();
  }
}

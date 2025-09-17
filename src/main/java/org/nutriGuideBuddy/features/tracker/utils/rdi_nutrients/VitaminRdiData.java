package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Vitamin Recommended Dietary Intake (RDI) data source.
 *
 * <p>Data source: <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t2/?report=objectonly">...</a>
 *
 * <p>Provides lookup of recommended daily intake for vitamins, based on age and gender.
 */
public class VitaminRdiData {

  private static final Map<AllowedNutrients, List<RdiRange>> vitaminRules =
      Map.ofEntries(
          Map.entry(
              AllowedNutrients.VitaminA,
              List.of(
                  new RdiRange(1, 3, null, 300),
                  new RdiRange(4, 8, null, 400),
                  new RdiRange(9, 13, Gender.MALE, 600),
                  new RdiRange(9, 13, Gender.FEMALE, 600),
                  new RdiRange(14, 150, Gender.MALE, 900),
                  new RdiRange(14, 150, Gender.FEMALE, 700))),
          Map.entry(
              AllowedNutrients.VitaminC,
              List.of(
                  new RdiRange(1, 3, null, 15),
                  new RdiRange(4, 8, null, 25),
                  new RdiRange(9, 13, Gender.MALE, 45),
                  new RdiRange(9, 13, Gender.FEMALE, 45),
                  new RdiRange(14, 18, Gender.MALE, 75),
                  new RdiRange(14, 18, Gender.FEMALE, 65),
                  new RdiRange(19, 150, Gender.MALE, 90),
                  new RdiRange(19, 150, Gender.FEMALE, 75))),
          Map.entry(
              AllowedNutrients.VitaminD_D2_D3,
              List.of(
                  new RdiRange(1, 8, null, 15),
                  new RdiRange(9, 70, null, 15),
                  new RdiRange(71, 150, null, 20))),
          Map.entry(
              AllowedNutrients.VitaminE,
              List.of(
                  new RdiRange(1, 3, null, 6),
                  new RdiRange(4, 8, null, 7),
                  new RdiRange(9, 13, null, 11),
                  new RdiRange(14, 150, null, 15))),
          Map.entry(
              AllowedNutrients.VitaminK,
              List.of(
                  new RdiRange(1, 3, null, 30),
                  new RdiRange(4, 8, null, 55),
                  new RdiRange(9, 13, Gender.MALE, 60),
                  new RdiRange(9, 13, Gender.FEMALE, 60),
                  new RdiRange(14, 18, Gender.MALE, 75),
                  new RdiRange(14, 18, Gender.FEMALE, 75),
                  new RdiRange(19, 150, Gender.MALE, 120),
                  new RdiRange(19, 150, Gender.FEMALE, 90))),
          Map.entry(
              AllowedNutrients.VitaminB1_Thiamin,
              List.of(
                  new RdiRange(1, 3, null, 0.5),
                  new RdiRange(4, 8, null, 0.6),
                  new RdiRange(9, 13, Gender.MALE, 0.9),
                  new RdiRange(9, 13, Gender.FEMALE, 0.9),
                  new RdiRange(14, 150, Gender.MALE, 1.2),
                  new RdiRange(14, 18, Gender.FEMALE, 1.0),
                  new RdiRange(19, 150, Gender.FEMALE, 1.1))),
          Map.entry(
              AllowedNutrients.VitaminB2_Riboflavin,
              List.of(
                  new RdiRange(1, 3, null, 0.5),
                  new RdiRange(4, 8, null, 0.6),
                  new RdiRange(9, 13, Gender.MALE, 0.9),
                  new RdiRange(9, 13, Gender.FEMALE, 0.9),
                  new RdiRange(14, 150, Gender.MALE, 1.3),
                  new RdiRange(14, 18, Gender.FEMALE, 1.0),
                  new RdiRange(19, 150, Gender.FEMALE, 1.1))),
          Map.entry(
              AllowedNutrients.VitaminB3_Niacin,
              List.of(
                  new RdiRange(1, 3, null, 6),
                  new RdiRange(4, 8, null, 8),
                  new RdiRange(9, 13, Gender.MALE, 12),
                  new RdiRange(9, 13, Gender.FEMALE, 12),
                  new RdiRange(14, 150, Gender.MALE, 16),
                  new RdiRange(14, 150, Gender.FEMALE, 14))),
          Map.entry(
              AllowedNutrients.VitaminB6,
              List.of(
                  new RdiRange(1, 3, null, 0.5),
                  new RdiRange(4, 8, null, 0.6),
                  new RdiRange(9, 13, null, 1.0),
                  new RdiRange(14, 50, Gender.MALE, 1.3),
                  new RdiRange(14, 18, Gender.FEMALE, 1.2),
                  new RdiRange(19, 50, Gender.FEMALE, 1.3),
                  new RdiRange(51, 150, Gender.MALE, 1.7),
                  new RdiRange(51, 150, Gender.FEMALE, 1.5))),
          Map.entry(
              AllowedNutrients.VitaminB9_Folate,
              List.of(
                  new RdiRange(1, 3, null, 150),
                  new RdiRange(4, 8, null, 200),
                  new RdiRange(9, 13, null, 300),
                  new RdiRange(14, 150, null, 400))),
          Map.entry(
              AllowedNutrients.VitaminB12,
              List.of(
                  new RdiRange(1, 3, null, 0.9),
                  new RdiRange(4, 8, null, 1.2),
                  new RdiRange(9, 13, null, 1.8),
                  new RdiRange(14, 150, null, 2.4))),
          Map.entry(
              AllowedNutrients.VitaminB5_PantothenicAcid,
              List.of(
                  new RdiRange(1, 3, null, 2),
                  new RdiRange(4, 8, null, 3),
                  new RdiRange(9, 13, null, 4),
                  new RdiRange(14, 150, null, 5))),
          Map.entry(
              AllowedNutrients.VitaminB7_Biotin,
              List.of(
                  new RdiRange(1, 3, null, 8),
                  new RdiRange(4, 8, null, 12),
                  new RdiRange(9, 13, null, 20),
                  new RdiRange(14, 18, null, 25),
                  new RdiRange(19, 150, null, 30))),
          Map.entry(
              AllowedNutrients.Choline,
              List.of(
                  new RdiRange(1, 3, null, 200),
                  new RdiRange(4, 8, null, 250),
                  new RdiRange(9, 13, Gender.MALE, 375),
                  new RdiRange(9, 13, Gender.FEMALE, 375),
                  new RdiRange(14, 150, Gender.MALE, 550),
                  new RdiRange(14, 18, Gender.FEMALE, 400),
                  new RdiRange(19, 150, Gender.FEMALE, 425))));

  /**
   * Returns the recommended daily intake for the given nutrient, gender, and age.
   *
   * @param nutrient vitamin nutrient
   * @param gender gender of the user
   * @param age age of the user
   * @return recommended intake in the nutrient’s unit, or 0.0 if no match found
   */
  public static double getRecommended(AllowedNutrients nutrient, Gender gender, int age) {
    return vitaminRules.getOrDefault(nutrient, List.of()).stream()
        .filter(r -> age >= r.minAge() && age <= r.maxAge())
        .filter(r -> r.gender() == null || r.gender() == gender)
        .map(RdiRange::value)
        .findFirst()
        .orElse(0.0);
  }

  public static Set<AllowedNutrients> getSupportedNutrients() {
    return vitaminRules.keySet();
  }
}

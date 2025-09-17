package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.user.enums.Gender;

/**
 * Macronutrient Recommended Daily Intake (RDI) and calculation rules.
 *
 * <p>Data: - <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t5/?report=objectonly">...</a>
 * - <a
 * href="https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t4/?report=objectonly">...</a>
 */
public class MacronutrientRdiData {
  private static final Map<AllowedNutrients, List<RdiRange>> macroRules =
      Map.ofEntries(
          Map.entry(
              AllowedNutrients.Fiber,
              List.of(
                  new RdiRange(1, 3, null, 19),
                  new RdiRange(4, 8, null, 25),
                  new RdiRange(9, 13, Gender.MALE, 31),
                  new RdiRange(9, 18, Gender.FEMALE, 26),
                  new RdiRange(14, 50, Gender.MALE, 38),
                  new RdiRange(14, 50, Gender.FEMALE, 25),
                  new RdiRange(51, 150, Gender.MALE, 30),
                  new RdiRange(51, 150, Gender.FEMALE, 21))),
          Map.entry(
              AllowedNutrients.Water,
              List.of(
                  new RdiRange(1, 3, null, 1.3),
                  new RdiRange(4, 8, null, 1.7),
                  new RdiRange(9, 13, Gender.MALE, 2.4),
                  new RdiRange(9, 13, Gender.FEMALE, 2.1),
                  new RdiRange(14, 18, Gender.MALE, 3.3),
                  new RdiRange(14, 18, Gender.FEMALE, 2.3),
                  new RdiRange(19, 150, Gender.MALE, 3.7),
                  new RdiRange(19, 150, Gender.FEMALE, 2.7))),
          Map.entry(
              AllowedNutrients.Sugar,
              List.of(
                  new RdiRange(2, 18, null, 25),
                  new RdiRange(19, 150, Gender.FEMALE, 25),
                  new RdiRange(19, 150, Gender.MALE, 36))),
          Map.entry(AllowedNutrients.Cholesterol, List.of(new RdiRange(0, 150, null, 300))),
          Map.entry(AllowedNutrients.Saturated_Fat, List.of(new RdiRange(0, 150, null, 0))),
          Map.entry(AllowedNutrients.Trans_Fat, List.of(new RdiRange(0, 150, null, 0))));

  /**
   * Returns the recommended daily intake for static macronutrients.
   *
   * @param nutrient the nutrient (e.g., Fiber, Water, Sugar)
   * @param gender user gender
   * @param age user age
   * @return recommended intake
   */
  public static double getRecommended(AllowedNutrients nutrient, Gender gender, int age) {
    return macroRules.getOrDefault(nutrient, List.of()).stream()
        .filter(r -> age >= r.minAge() && age <= r.maxAge())
        .filter(r -> r.gender() == null || r.gender() == gender)
        .map(RdiRange::value)
        .findFirst()
        .orElse(0.0);
  }

  public static Set<AllowedNutrients> getSupportedNutrients() {
    return macroRules.keySet();
  }
}

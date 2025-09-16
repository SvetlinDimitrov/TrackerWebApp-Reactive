package org.nutriGuideBuddy.features.record.utils;

import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;

public class CalculateMacros {

  /*
  Calculate Macros:
  Protein: 25% of 2946.647 calorie = (0.25 × 2946.647) / 4 calorie per gram = 183.29 grams
  Carbohydrates: 50% of 2946.647 calorie = (0.50 × 2946.647) / 4 calorie per gram = 367.50 grams
  Fats: 25% of 2946.647 calorie = (0.25 × 2946.647) / 9 calorie per gram = 109.29 grams
  */
  public static double calculateMacros(
      String name, double dailyConsumedCalories, double distributedMacros) {
    double raw = distributedMacros * dailyConsumedCalories;
    double result;
    if (name.equals(AllowedNutrients.Protein.getNutrientName())
        || name.equals(AllowedNutrients.Carbohydrate.getNutrientName())) {
      result = raw / 4.0;
    } else {
      result = raw / 9.0;
    }
    return Math.floor(result);
  }
}

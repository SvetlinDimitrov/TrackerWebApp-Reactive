package org.nutriGuideBuddy.features.record.utils;

import org.nutriGuideBuddy.features.user.enums.Gender;

public class BMRCalc {

  /**
   * Calculate Basal Metabolic Rate (BMR): This is the number of calorie your body needs to maintain
   * basic physiological functions while at rest. The most common formula for calculating BMR is the
   * Harris-Benedict equation. There are separate formulas for men and women:
   *
   * <p>For men: BMR = 88.362 + (13.397 × weight in kg) + (4.799 × height in cm) - (5.677 × age in
   * years) For women: BMR = 447.593 + (9.247 × weight in kg) + (3.098 × height in cm) - (4.330 ×
   * age in years)
   */
  public static double calculateBMR(Gender gender, double kg, double height, double age) {
    if (gender.equals(Gender.MALE)) {
      return 88.362 + (13.397 * kg) + (4.799 * height) - (5.677 * age);
    } else {
      return 447.593 + (9.247 * kg) + (3.098 * height) - (4.330 * age);
    }
  }
}

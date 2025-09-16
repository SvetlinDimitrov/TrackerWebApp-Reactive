package org.nutriGuideBuddy.features.record.utils;

import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.NutritionIntakeView;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.record.dto.DistributedMacros;
import org.nutriGuideBuddy.features.user.enums.Gender;

public class MacronutrientCreator {

  /*
  data : https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t5/?report=objectonly
  more : https://www.ncbi.nlm.nih.gov/books/NBK56068/table/summarytables.t4/?report=objectonly
  */

  public static final Set<String> allAllowedMacros =
      Set.of(
          AllowedNutrients.Carbohydrate.getNutrientName(),
          AllowedNutrients.Protein.getNutrientName(),
          AllowedNutrients.Fat.getNutrientName(),
          AllowedNutrients.Fiber.getNutrientName(),
          AllowedNutrients.Sugar.getNutrientName(),
          AllowedNutrients.Omega6.getNutrientName(),
          AllowedNutrients.Omega3.getNutrientName(),
          AllowedNutrients.Water.getNutrientName(),
          AllowedNutrients.Cholesterol.getNutrientName(),
          AllowedNutrients.Saturated_Fat.getNutrientName(),
          AllowedNutrients.Trans_Fat.getNutrientName());

  public static Map<String, NutritionIntakeView> fillMacros(
      Map<String, NutritionIntakeView> nutrientMap,
      double caloriesPerDay,
      Gender gender,
      DistributedMacros distributedMacros,
      int age) {

    NutritionIntakeView fat =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Fat.getNutrientName())
            .measurement(AllowedNutrients.Fat.getNutrientUnit())
            .recommendedIntake(
                CalculateMacros.calculateMacros(
                    AllowedNutrients.Fat.getNutrientName(),
                    caloriesPerDay,
                    distributedMacros.fat()))
            .dailyConsumed(0.0)
            .build();
    NutritionIntakeView protein =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Protein.getNutrientName())
            .measurement(AllowedNutrients.Protein.getNutrientUnit())
            .recommendedIntake(
                CalculateMacros.calculateMacros(
                    AllowedNutrients.Protein.getNutrientName(),
                    caloriesPerDay,
                    distributedMacros.protein()))
            .dailyConsumed(0.0)
            .build();
    NutritionIntakeView carbs =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Carbohydrate.getNutrientName())
            .measurement(AllowedNutrients.Carbohydrate.getNutrientUnit())
            .recommendedIntake(
                CalculateMacros.calculateMacros(
                    AllowedNutrients.Carbohydrate.getNutrientName(),
                    caloriesPerDay,
                    distributedMacros.carbs()))
            .dailyConsumed(0.0)
            .build();
    NutritionIntakeView omega6 =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Omega6.getNutrientName())
            .measurement(AllowedNutrients.Omega6.getNutrientUnit())
            .recommendedIntake(
                CalculateMacros.calculateMacros(
                    AllowedNutrients.Omega6.getNutrientName(),
                    caloriesPerDay,
                    distributedMacros.omega6()))
            .dailyConsumed(0.0)
            .build();
    NutritionIntakeView omega3 =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Omega3.getNutrientName())
            .measurement(AllowedNutrients.Omega3.getNutrientUnit())
            .recommendedIntake(
                CalculateMacros.calculateMacros(
                    AllowedNutrients.Omega3.getNutrientName(),
                    caloriesPerDay,
                    distributedMacros.omega3()))
            .dailyConsumed(0.0)
            .build();

    nutrientMap.put(fat.getName(), fat);
    nutrientMap.put(protein.getName(), protein);
    nutrientMap.put(carbs.getName(), carbs);
    nutrientMap.put(omega6.getName(), omega6);
    nutrientMap.put(omega3.getName(), omega3);
    fillFiber(nutrientMap, gender, age);
    fillWater(nutrientMap, gender, age);
    fillSugar(nutrientMap, gender, age);
    fillCholesterol(nutrientMap);
    fillSaturatedFat(nutrientMap);
    fillTransFat(nutrientMap);
    return nutrientMap;
  }

  private static void fillFiber(
      Map<String, NutritionIntakeView> nutrientMap, Gender gender, Integer age) {
    NutritionIntakeView fiber =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Fiber.getNutrientName())
            .measurement(AllowedNutrients.Fiber.getNutrientUnit())
            .dailyConsumed(0.0)
            .build();

    double dailyIntake = 0.0;

    if (ageBetween(1, 3, age)) {
      dailyIntake = 19.0;
    } else if (ageBetween(4, 8, age)) {
      dailyIntake = 25.0;
    } else {
      switch (gender) {
        case MALE -> {
          if (ageBetween(9, 13, age)) {
            dailyIntake = 31.0;
          } else if (ageBetween(14, 50, age)) {
            dailyIntake = 38.0;
          } else {
            dailyIntake = 30.0;
          }
        }
        case FEMALE -> {
          if (ageBetween(9, 18, age)) {
            dailyIntake = 26.0;
          } else if (ageBetween(19, 50, age)) {
            dailyIntake = 25.0;
          } else {
            dailyIntake = 21.0;
          }
        }
      }
    }
    fiber.setRecommendedIntake(dailyIntake);
    nutrientMap.put(fiber.getName(), fiber);
  }

  private static void fillSaturatedFat(Map<String, NutritionIntakeView> nutrientMap) {
    NutritionIntakeView fat =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Saturated_Fat.getNutrientName())
            .measurement(AllowedNutrients.Saturated_Fat.getNutrientUnit())
            .dailyConsumed(0.0)
            .build();

    double dailyIntake = 0.0;

    fat.setRecommendedIntake(dailyIntake);
    nutrientMap.put(fat.getName(), fat);
  }

  private static void fillTransFat(Map<String, NutritionIntakeView> nutrientMap) {
    NutritionIntakeView fat =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Trans_Fat.getNutrientName())
            .measurement(AllowedNutrients.Trans_Fat.getNutrientUnit())
            .dailyConsumed(0.0)
            .build();

    double dailyIntake = 0.0;

    fat.setRecommendedIntake(dailyIntake);
    nutrientMap.put(fat.getName(), fat);
  }

  private static void fillWater(
      Map<String, NutritionIntakeView> nutrientMap, Gender gender, Integer age) {
    NutritionIntakeView water =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Water.getNutrientName())
            .measurement(AllowedNutrients.Water.getNutrientUnit())
            .dailyConsumed(0.0)
            .build();

    double dailyIntake = 0.0;

    if (ageBetween(1, 3, age)) {
      dailyIntake = 1.3;
    } else if (ageBetween(4, 8, age)) {
      dailyIntake = 1.7;
    } else {
      switch (gender) {
        case MALE -> {
          if (ageBetween(9, 13, age)) {
            dailyIntake = 2.4;
          } else if (ageBetween(14, 18, age)) {
            dailyIntake = 3.3;
          } else {
            dailyIntake = 3.7;
          }
        }
        case FEMALE -> {
          if (ageBetween(9, 13, age)) {
            dailyIntake = 2.1;
          } else if (ageBetween(14, 18, age)) {
            dailyIntake = 2.3;
          } else {
            dailyIntake = 2.7;
          }
        }
      }
    }
    water.setRecommendedIntake(dailyIntake);
    nutrientMap.put(water.getName(), water);
  }

  private static void fillSugar(
      Map<String, NutritionIntakeView> nutrientMap, Gender gender, Integer age) {
    NutritionIntakeView sugar =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Sugar.getNutrientName())
            .measurement(AllowedNutrients.Sugar.getNutrientUnit())
            .dailyConsumed(0.0)
            .build();

    double recommendedIntake;
    if (age >= 2 && age <= 18) {
      recommendedIntake = 25.0;
    } else if (gender == Gender.FEMALE) {
      recommendedIntake = 25.0;
    } else {
      recommendedIntake = 36.0;
    }
    sugar.setRecommendedIntake(recommendedIntake);

    nutrientMap.put(sugar.getName(), sugar);
  }

  private static void fillCholesterol(Map<String, NutritionIntakeView> nutrientMap) {
    NutritionIntakeView cholesterol =
        NutritionIntakeView.builder()
            .name(AllowedNutrients.Cholesterol.getNutrientName())
            .measurement(AllowedNutrients.Cholesterol.getNutrientUnit())
            .recommendedIntake(300.0)
            .dailyConsumed(0.0)
            .build();

    nutrientMap.put(cholesterol.getName(), cholesterol);
  }

  private static boolean ageBetween(Integer minAge, Integer maxAge, Integer age) {
    return (age >= minAge && age <= maxAge);
  }
}

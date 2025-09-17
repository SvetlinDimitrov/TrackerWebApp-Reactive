package org.nutriGuideBuddy.features.tracker.utils;

import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import reactor.core.publisher.Mono;

public class CalorieCalculator {

  /**
   * Calculates the daily calorie goal for a user based on their details and goal.
   *
   * @param userDetails the user’s personal details (weight, height, age, gender, workout state)
   * @param goal the user’s goal (maintain, lose, gain weight)
   * @return Mono emitting the daily calorie target
   */
  public static Mono<Double> calculateDailyCalories(UserDetailsView userDetails, Goals goal) {
    if (userDetails == null || goal == null) {
      return Mono.error(new IllegalArgumentException("UserDetails and goal must not be null"));
    }

    Double kg = userDetails.kilograms();
    Double height = userDetails.height();
    Integer age = userDetails.age();
    Gender gender = userDetails.gender();
    WorkoutState workoutState = userDetails.workoutState();

    if (kg == null || height == null || age == null || gender == null || workoutState == null) {
      return Mono.error(
          new IllegalArgumentException(
              "UserDetails must have weight, height, age, gender, and workout state"));
    }

    // Step 1: Calculate BMR using Harris-Benedict equation
    double bmr =
        gender == Gender.MALE
            ? 88.362 + (13.397 * kg) + (4.799 * height) - (5.677 * age)
            : 447.593 + (9.247 * kg) + (3.098 * height) - (4.330 * age);

    // Step 2: Apply activity factor (TDEE)
    double dailyCalories = getDailyCalories(goal, workoutState, bmr);

    return Mono.just(dailyCalories);
  }

  private static double getDailyCalories(Goals goal, WorkoutState workoutState, double bmr) {
    double calories =
        switch (workoutState) {
          case SEDENTARY -> bmr * 1.2;
          case LIGHTLY_ACTIVE -> bmr * 1.375;
          case MODERATELY_ACTIVE -> bmr * 1.55;
          case VERY_ACTIVE -> bmr * 1.725;
          case SUPER_ACTIVE -> bmr * 1.9;
        };

    // Step 3: Adjust based on goal
    return switch (goal) {
      case MAINTAIN_WEIGHT -> calories;
      case LOSE_WEIGHT -> calories - 500;
      case GAIN_WEIGHT -> calories + 500;
    };
  }
}

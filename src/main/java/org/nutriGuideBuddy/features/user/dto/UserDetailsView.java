package org.nutriGuideBuddy.features.user.dto;

import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;

public record UserDetailsView(
    String id,
    Double kilograms,
    Double height,
    Integer age,
    WorkoutState workoutState,
    Gender gender,
    Goals goal,
    DietType diet,
    NutritionAuthority nutritionAuthority) {}

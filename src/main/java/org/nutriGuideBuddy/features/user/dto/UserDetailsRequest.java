package org.nutriGuideBuddy.features.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;

public record UserDetailsRequest(
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double kilograms,
    @DecimalMin(value = "20", message = "must be at least 20") Double height,
    @Min(value = 1, message = "must be greater than 0") Integer age,
    WorkoutState workoutState,
    Gender gender,
    Goals goal,
    DietType diet,
    NutritionAuthority authority) {}

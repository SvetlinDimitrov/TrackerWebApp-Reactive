package org.nutriGuideBuddy.features.user_details.dto;

import org.nutriGuideBuddy.features.user_details.enums.Gender;
import org.nutriGuideBuddy.features.user_details.enums.WorkoutState;

public record UserDetailsView(
    String id,
    Double kilograms,
    Double height,
    Integer age,
    WorkoutState workoutState,
    Gender gender) {}

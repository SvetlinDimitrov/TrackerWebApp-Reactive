package org.nutriGuideBuddy.features.user_details.dto;

import java.math.BigDecimal;
import org.nutriGuideBuddy.features.user_details.enums.Gender;
import org.nutriGuideBuddy.features.user_details.enums.WorkoutState;

public record UserDetailsView(
    String id,
    BigDecimal kilograms,
    BigDecimal height,
    Integer age,
    WorkoutState workoutState,
    Gender gender) {}

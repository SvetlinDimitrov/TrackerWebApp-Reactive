package org.nutriGuideBuddy.domain.dto.user_details;

import java.math.BigDecimal;
import org.nutriGuideBuddy.domain.enums.Gender;
import org.nutriGuideBuddy.domain.enums.WorkoutState;

public record UserDetailsView(
    String id,
    BigDecimal kilograms,
    BigDecimal height,
    Integer age,
    WorkoutState workoutState,
    Gender gender) {}

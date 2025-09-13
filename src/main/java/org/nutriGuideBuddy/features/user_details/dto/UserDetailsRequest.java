package org.nutriGuideBuddy.features.user_details.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

import org.nutriGuideBuddy.features.user_details.annotaions.ValidGender;
import org.nutriGuideBuddy.features.user_details.annotaions.ValidWorkoutState;

public record UserDetailsRequest(
    @DecimalMin(value = "0.1", message = "must be greater than 0") BigDecimal kilograms,
    @DecimalMin(value = "20", message = "must be at least 20") BigDecimal height,
    @Min(value = 1, message = "must be greater than 0") Integer age,
    @ValidWorkoutState String workoutState,
    @ValidGender String gender) {}

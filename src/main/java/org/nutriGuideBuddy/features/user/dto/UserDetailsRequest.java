package org.nutriGuideBuddy.features.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.nutriGuideBuddy.features.user.annotations.ValidGender;
import org.nutriGuideBuddy.features.user.annotations.ValidWorkoutState;

public record UserDetailsRequest(
    @DecimalMin(value = "0.1", message = "must be greater than 0") Double kilograms,
    @DecimalMin(value = "20", message = "must be at least 20") Double height,
    @Min(value = 1, message = "must be greater than 0") Integer age,
    @ValidWorkoutState String workoutState,
    @ValidGender String gender) {}

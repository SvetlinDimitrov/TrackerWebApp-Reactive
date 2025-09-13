package org.nutriGuideBuddy.domain.dto.user_details;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

import org.nutriGuideBuddy.annotaions.ValidGender;
import org.nutriGuideBuddy.annotaions.ValidWorkoutState;

public record UserDetailsRequest(
    @DecimalMin(value = "0.1", message = "must be greater than 0") BigDecimal kilograms,
    @DecimalMin(value = "20", message = "must be at least 20") BigDecimal height,
    @Min(value = 1, message = "must be greater than 0") Integer age,
    @ValidWorkoutState String workoutState,
    @ValidGender String gender) {}

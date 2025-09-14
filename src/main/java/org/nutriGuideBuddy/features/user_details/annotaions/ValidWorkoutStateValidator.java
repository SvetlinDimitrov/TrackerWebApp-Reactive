package org.nutriGuideBuddy.features.user_details.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nutriGuideBuddy.features.user_details.enums.WorkoutState;

public class ValidWorkoutStateValidator implements ConstraintValidator<ValidWorkoutState, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;

    try {
      WorkoutState.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Invalid workout state. Valid options are: " + WorkoutState.validValues())
          .addConstraintViolation();
      return false;
    }
  }
}

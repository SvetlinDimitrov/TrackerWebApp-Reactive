package org.nutriGuideBuddy.features.tracker.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.tracker.enums.Goals;

public class ValidGoalValidator implements ConstraintValidator<ValidGoal, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;
    }

    try {
      Goals.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "invalid goal. Valid options are: "
                  + Arrays.stream(Goals.values()).map(Enum::name).collect(Collectors.joining(", ")))
          .addConstraintViolation();
      return false;
    }
  }
}

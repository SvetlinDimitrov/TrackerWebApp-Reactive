package org.nutriGuideBuddy.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nutriGuideBuddy.domain.enums.Gender;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    try {
      Gender.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
              "Invalid gender. Valid options are: " + Gender.validValues())
          .addConstraintViolation();
      return false;
    }
  }
}

package org.nutriGuideBuddy.features.user_details.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nutriGuideBuddy.features.user_details.enums.Gender;

public class ValidGenderValidator implements ConstraintValidator<ValidGender, String> {
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
      context
          .buildConstraintViolationWithTemplate(
              "Invalid gender. Valid options are: " + Gender.validValues())
          .addConstraintViolation();
      return false;
    }
  }
}

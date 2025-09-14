package org.nutriGuideBuddy.features.user.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nutriGuideBuddy.features.user.enums.UserRole;

public class ValidUserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;

    try {
      UserRole.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "invalid user role: " + value + ". Valid options are: " + UserRole.validValues())
          .addConstraintViolation();
      return false;
    }
  }
}

package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;

public class OnlyOneMainServingValidator
    implements ConstraintValidator<OnlyOneMainServing, Set<ServingCreateRequest>> {
  @Override
  public boolean isValid(Set<ServingCreateRequest> servings, ConstraintValidatorContext context) {
    if (servings == null || servings.isEmpty()) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Exactly one serving must be marked as main")
          .addConstraintViolation();
      return false;
    }
    long mainCount = servings.stream().filter(ServingCreateRequest::main).count();
    if (mainCount != 1) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Exactly one serving must be marked as main")
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}

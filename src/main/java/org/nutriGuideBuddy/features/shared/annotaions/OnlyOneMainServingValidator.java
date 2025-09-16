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
      return true;
    }

    long mainCount =
        servings.stream().filter(s -> s != null && Boolean.TRUE.equals(s.main())).count();

    return mainCount == 1;
  }
}

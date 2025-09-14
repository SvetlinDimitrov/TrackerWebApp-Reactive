package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;

public class ValidSortDirectionValidator
    implements ConstraintValidator<ValidSortDirection, Map<String, String>> {

  @Override
  public boolean isValid(Map<String, String> sort, ConstraintValidatorContext context) {
    if (sort == null || sort.isEmpty()) return true;

    boolean allValid = true;

    for (Map.Entry<String, String> entry : sort.entrySet()) {
      String direction = entry.getValue();
      String fieldName = entry.getKey();

      if (direction == null || direction.isBlank()) {
        continue;
      }

      direction = direction.toUpperCase();

      if (!"ASC".equals(direction) && !"DESC".equals(direction)) {
        allValid = false;
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "invalid sort direction: "
                    + entry.getValue()
                    + ". "
                    + "Valid options are: 'ASC' or 'DESC'.")
            .addPropertyNode("value")
            .addConstraintViolation();
      }
    }

    return allValid;
  }
}

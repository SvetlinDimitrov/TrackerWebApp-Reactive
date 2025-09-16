package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;

public class AllowedNutrientValidator implements ConstraintValidator<AllowedNutrient, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean valid =
        value != null
            && java.util.Arrays.stream(AllowedNutrients.values())
                .anyMatch(n -> n.getNutrientName().equals(value));
    if (!valid) {
      String validNames =
          java.util.Arrays.stream(AllowedNutrients.values())
              .map(AllowedNutrients::getNutrientName)
              .collect(Collectors.joining(", "));
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "invalid name: " + value + ". Valid ones: " + validNames)
          .addConstraintViolation();
    }
    return valid;
  }
}

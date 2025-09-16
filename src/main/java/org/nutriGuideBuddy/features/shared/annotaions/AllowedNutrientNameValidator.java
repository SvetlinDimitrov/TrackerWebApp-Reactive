package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;

public class AllowedNutrientNameValidator
    implements ConstraintValidator<AllowedNutrientName, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;
    }

    boolean exists =
        Arrays.stream(AllowedNutrients.values()).anyMatch(n -> n.getNutrientName().equals(value));

    if (!exists) {
      String validNames =
          Arrays.stream(AllowedNutrients.values())
              .map(AllowedNutrients::getNutrientName)
              .collect(Collectors.joining(", "));

      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Invalid nutrient name: " + value + ". Valid names: " + validNames)
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}

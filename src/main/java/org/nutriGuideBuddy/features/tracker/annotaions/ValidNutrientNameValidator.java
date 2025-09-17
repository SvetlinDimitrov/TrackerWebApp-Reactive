package org.nutriGuideBuddy.features.tracker.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.annotations.ValidNutrientName;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;

public class ValidNutrientNameValidator implements ConstraintValidator<ValidNutrientName, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;
    }

    boolean exists =
        Arrays.stream(AllowedNutrients.values())
            .anyMatch(n -> n.getNutrientName().equalsIgnoreCase(value));

    if (!exists) {
      String validNames =
          Arrays.stream(AllowedNutrients.values())
              .map(AllowedNutrients::getNutrientName)
              .collect(Collectors.joining(", "));

      buildViolation(
          context, "invalid nutrient name: " + value + ". Valid names are: " + validNames);
      return false;
    }

    return true;
  }

  private void buildViolation(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}

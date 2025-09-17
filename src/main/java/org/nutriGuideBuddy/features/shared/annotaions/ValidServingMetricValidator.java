package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;

public class ValidServingMetricValidator
    implements ConstraintValidator<ValidServingMetric, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true; // @NotNull should handle null separately
    }

    try {
      ServingMetric.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "invalid serving metric. Valid options are: "
                  + Arrays.stream(ServingMetric.values())
                      .map(Enum::name)
                      .collect(Collectors.joining(", ")))
          .addConstraintViolation();
      return false;
    }
  }
}

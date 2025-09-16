package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidImageUrlValidator implements ConstraintValidator<ValidImageUrl, String> {
  private static final String IMAGE_URL_REGEX =
      "\\bhttps?://\\S+\\.(?:png|jpe?g|gif|bmp|svg)(?:\\?\\S*)?\\b";
  private static final String IMAGE_URL_REGEX2 =
      "\\bhttps?://(?:\\w+\\.)?assets\\.syndigo\\.com/(?:\\w+-){4}\\w+\\b";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;
    return value.matches(IMAGE_URL_REGEX) || value.matches(IMAGE_URL_REGEX2);
  }
}

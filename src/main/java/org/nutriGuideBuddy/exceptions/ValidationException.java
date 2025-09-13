package org.nutriGuideBuddy.exceptions;

import java.util.Map;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

  private final Map<String, String> errors;

  public ValidationException(Map<String, String> errors) {
    super("Validation failed for one or more fields");
    this.errors = errors;
  }
}

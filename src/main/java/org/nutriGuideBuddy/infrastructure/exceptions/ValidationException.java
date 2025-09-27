package org.nutriGuideBuddy.infrastructure.exceptions;

import java.util.Map;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

  private final String resource;
  private final Map<String, String> errors;

  private ValidationException(String message, String resource, Map<String, String> errors) {
    super(message);
    this.resource = resource;
    this.errors = (errors == null ? Map.of() : Map.copyOf(errors));
  }

  /** Generic: "<resource> validation failed" (or just "Validation failed" if resource null) */
  public static ValidationException of(String resource, Map<String, String> errors) {
    String title =
        (resource == null || resource.isBlank())
            ? "Validation failed"
            : resource + " validation failed";
    return new ValidationException(title, resource, errors);
  }

  /** Single-field helper: field -> message */
  public static ValidationException field(String resource, String field, String message) {
    return of(resource, Map.of(field, message));
  }

  /** Convenience for the super common case */
  public static ValidationException duplicate(String resource, String field) {
    return field(resource, field, "already exists");
  }

  /** If you already have a custom top-level message */
  public static ValidationException message(String message, Map<String, String> errors) {
    return new ValidationException(message, null, errors);
  }
}

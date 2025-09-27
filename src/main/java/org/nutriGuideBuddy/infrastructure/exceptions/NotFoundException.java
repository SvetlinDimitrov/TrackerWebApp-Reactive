package org.nutriGuideBuddy.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {
  private String resource;
  private String field;
  private Object value;

  private NotFoundException(String message) {
    super(message);
  }

  public static NotFoundException message(String message) {
    return new NotFoundException(message);
  }

  public static NotFoundException of(String resource) {
    NotFoundException ex = new NotFoundException(resource + " not found.");
    ex.resource = resource;
    return ex;
  }

  public static NotFoundException byId(String resource, Object id) {
    NotFoundException ex = new NotFoundException(resource + " not found with id: " + id);
    ex.resource = resource;
    ex.field = "id";
    ex.value = id;
    return ex;
  }

  public static NotFoundException by(String resource, String field, Object value) {
    NotFoundException ex =
        new NotFoundException(resource + " not found by " + field + ": " + value);
    ex.resource = resource;
    ex.field = field;
    ex.value = value;
    return ex;
  }
}

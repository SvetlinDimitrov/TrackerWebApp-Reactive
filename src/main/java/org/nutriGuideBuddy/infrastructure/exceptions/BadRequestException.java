package org.nutriGuideBuddy.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends RuntimeException {
  private String resource;
  private String reason;

  private BadRequestException(String message) {
    super(message);
  }

  // Pass-through when you already have a final message
  public static BadRequestException message(String message) {
    return new BadRequestException(message);
  }

  // "<resource> bad request."
  public static BadRequestException of(String resource) {
    BadRequestException ex = new BadRequestException(resource + " bad request.");
    ex.resource = resource;
    return ex;
  }

  // "<resource> bad request: <reason>"
  public static BadRequestException of(String resource, String reason) {
    BadRequestException ex = new BadRequestException(resource + " bad request: " + reason);
    ex.resource = resource;
    ex.reason = reason;
    return ex;
  }
}

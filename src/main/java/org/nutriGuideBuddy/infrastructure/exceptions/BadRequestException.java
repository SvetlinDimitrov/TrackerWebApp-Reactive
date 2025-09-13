package org.nutriGuideBuddy.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends Exception {
  private final String message;

  public BadRequestException(String message) {
    super(message);
    this.message = message;
  }
}

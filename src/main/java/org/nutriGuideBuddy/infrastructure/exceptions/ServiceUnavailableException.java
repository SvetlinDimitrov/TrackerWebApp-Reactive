package org.nutriGuideBuddy.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceUnavailableException extends RuntimeException {
  private String service;
  private String reason;

  private ServiceUnavailableException(String message) {
    super(message);
  }

  // When you already have a final message
  public static ServiceUnavailableException message(String message) {
    return new ServiceUnavailableException(message);
  }

  // "<service> service is temporarily unavailable."
  public static ServiceUnavailableException of(String service) {
    ServiceUnavailableException ex =
        new ServiceUnavailableException(service + " service is temporarily unavailable.");
    ex.service = service;
    return ex;
  }

  // "<service> service is temporarily unavailable: <reason>"
  public static ServiceUnavailableException of(String service, String reason) {
    ServiceUnavailableException ex =
        new ServiceUnavailableException(service + " service is temporarily unavailable: " + reason);
    ex.service = service;
    ex.reason = reason;
    return ex;
  }

  // Same as above but preserves the original cause
  public static ServiceUnavailableException withCause(
      String service, String reason, Throwable cause) {
    ServiceUnavailableException ex =
        new ServiceUnavailableException(service + " service is temporarily unavailable: " + reason);
    ex.service = service;
    ex.reason = reason;
    ex.initCause(cause);
    return ex;
  }
}

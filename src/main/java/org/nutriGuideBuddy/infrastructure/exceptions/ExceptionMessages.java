package org.nutriGuideBuddy.infrastructure.exceptions;

public class ExceptionMessages {
  public static final String USER_NOT_FOUND = "User not found with the provided email.";
  public static final String NOT_FOUND_BY_ID = "%s not found with id: %s";
  public static final String USER_NOT_FOUND_BY_ID = "User not found with id: %s";
  public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: %s";
  public static final String USER_DETAILS_NOT_FOUND_BY_ID = "User details not found with id: %s";
  public static final String USER_DETAILS_NOT_FOUND_FOR_USER_ID =
      "User details not found for userId: %s";
  public static final String PRINCIPAL_NOT_FOUND =
      "Authenticated user not found in the security context.";
  public static final String INVALID_JWT_TOKEN = "Invalid JWT token.";
  public static final String INVALID_CREDENTIALS = "Invalid credentials.";
}

package org.nutriGuideBuddy.infrastructure.exceptions;

public class ExceptionMessages {
  public static final String INVALID_JWT_TOKEN = "Invalid JWT token.";
  public static final String INVALID_CREDENTIALS = "Invalid credentials.";
  public static final String SERVINGS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID =
      "Serving with ids %s does not belong to %s with id %s";
  public static final String NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID =
      "Nutrition with ids %s do not belong to %s with id %s";
  public static final String EXACTLY_ONE_MAIN_SERVING_AFTER_UPDATE =
      "After update, exactly one serving must be marked as main. Found: %s";
}

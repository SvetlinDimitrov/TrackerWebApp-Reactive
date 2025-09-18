package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.types.StandardRdiData;

public class RdiProviderFactory {

  public enum DietType {
    STANDARD
    // Later: KETO, VEGAN, MEDITERRANEAN, etc.
  }

  public static RdiProvider getProvider(DietType type) {
    return switch (type) {
      case STANDARD -> new StandardRdiData();
    };
  }
}

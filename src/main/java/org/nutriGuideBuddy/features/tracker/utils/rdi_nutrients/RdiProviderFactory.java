package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.types.StandardRdiData;
import org.nutriGuideBuddy.features.user.enums.DuetTypes;

public class RdiProviderFactory {
  public static RdiProvider getProvider(DuetTypes type) {
    return switch (type) {
      case STANDARD -> new StandardRdiData();
    };
  }
}

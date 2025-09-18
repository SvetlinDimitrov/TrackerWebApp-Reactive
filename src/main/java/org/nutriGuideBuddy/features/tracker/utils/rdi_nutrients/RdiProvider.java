package org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.user.enums.Gender;

public interface RdiProvider {

  double getRecommended(AllowedNutrients nutrient, Gender gender, int age);

  Set<AllowedNutrients> getSupportedNutrients();
}

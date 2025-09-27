package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Optional;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;

public class AllowedNutrientUnitValidator
    implements ConstraintValidator<AllowedNutrientUnit, NutritionCreateRequest> {

  @Override
  public boolean isValid(NutritionCreateRequest request, ConstraintValidatorContext context) {
    if (request == null || request.name() == null || request.unit() == null) {
      return true;
    }

    Optional<AllowedNutrients> nutrient =
        Arrays.stream(AllowedNutrients.values())
            .filter(n -> n.getNutrientName().equals(request.name()))
            .findFirst();

    if (nutrient.isPresent()) {
      String expectedUnit = nutrient.get().getNutrientUnit();
      if (!expectedUnit.equals(request.unit())) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Invalid unit for "
                    + request.name()
                    + ": "
                    + request.unit()
                    + ". Expected unit: "
                    + expectedUnit)
            .addPropertyNode("unit")
            .addConstraintViolation();
        return false;
      }
    }

    return true;
  }
}

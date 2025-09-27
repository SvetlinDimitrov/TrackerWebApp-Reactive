package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedNutrientUnitValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedNutrientUnit {
  String message() default "Invalid nutrient unit for given name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

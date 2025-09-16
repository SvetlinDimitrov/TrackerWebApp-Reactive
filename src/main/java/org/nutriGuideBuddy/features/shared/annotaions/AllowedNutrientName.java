package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedNutrientNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedNutrientName {
  String message() default "Invalid nutrient name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

package org.nutriGuideBuddy.features.shared.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.nutriGuideBuddy.features.tracker.annotaions.ValidNutrientNameValidator;

@Documented
@Constraint(validatedBy = ValidNutrientNameValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ValidNutrientName {

  String message() default "Invalid nutrient name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedNutrientValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedNutrient {
    String message() default "Invalid nutrition name or unit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
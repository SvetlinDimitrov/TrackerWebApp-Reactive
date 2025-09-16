package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OnlyOneMainServingValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyOneMainServing {
    String message() default "Exactly one serving must be marked as main";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
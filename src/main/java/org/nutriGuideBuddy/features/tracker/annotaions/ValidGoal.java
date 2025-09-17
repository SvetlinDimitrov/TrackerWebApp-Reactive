package org.nutriGuideBuddy.features.tracker.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidGoalValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGoal {
  String message() default "Invalid goal";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

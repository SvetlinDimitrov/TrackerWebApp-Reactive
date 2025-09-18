package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidServingMetricValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidServingMetric {
  String message() default "Invalid serving metric";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

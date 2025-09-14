package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidSortDirectionValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortDirection {
  String message() default "Invalid sort direction. Valid options are: 'ASC' or 'DESC'.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
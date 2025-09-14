package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidSortFieldsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortFields {
  String message() default "Invalid sort fields. Allowed fields are: ";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> entity();

  String[] excludeFields() default {}; // New attribute for excluding specific fields
}

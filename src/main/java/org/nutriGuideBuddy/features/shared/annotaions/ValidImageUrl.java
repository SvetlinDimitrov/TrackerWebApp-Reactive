package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageUrlValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageUrl {
  String message() default "Invalid image URL";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

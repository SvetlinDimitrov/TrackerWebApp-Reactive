package org.nutriGuideBuddy.features.user.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserRoleValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserRole {
  String message() default "Invalid user role";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
package org.nutriGuideBuddy.annotaions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkoutStateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkoutState {
    String message() default "Invalid workout state";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
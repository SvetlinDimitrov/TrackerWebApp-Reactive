package org.nutriGuideBuddy.features.meal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MealCreateRequest(
    @NotBlank(message = "must not be blank.")
        @Size(min = 1, max = 255, message = "must be between 1 and 255 characters.")
        String name,
    @PastOrPresent(message = "must be in the past or present.") LocalDate createdAt) {}

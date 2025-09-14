package org.nutriGuideBuddy.features.meal.dto;

import jakarta.validation.constraints.Size;

public record MealUpdateRequest(
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters.") String name) {}

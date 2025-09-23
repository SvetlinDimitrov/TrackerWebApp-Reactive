package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.annotaions.OnlyOneMainServing;
import org.nutriGuideBuddy.features.shared.annotaions.ValidImageUrl;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;

public record FoodCreateRequest(
    @NotBlank(message = "cannot be blank")
        @Size(min = 1, max = 255, message = "must be between 1 and 255 characters")
        String name,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String info,
    @Size(min = 1, max = 65535, message = "must be between 1 and 65535 characters")
        String largeInfo,
    @ValidImageUrl(message = "must be a valid image URL") String picture,
    @NotNull(message = "is required")
        @DecimalMin(value = "0.01", message = "must be a positive number")
        Double calorieAmount,
    @NotNull(message = "is required") CalorieUnits calorieUnit,
    @OnlyOneMainServing Set<@Valid ServingCreateRequest> servings,
    Set<@Valid NutritionCreateRequest> nutrients) {}

package org.nutriGuideBuddy.features.meal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.annotaions.OnlyOneMainServing;
import org.nutriGuideBuddy.features.shared.annotaions.ValidImageUrl;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;

public record MealFoodUpdateRequest(
    @NotNull(message = "is required") Long id,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String name,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String info,
    @Size(min = 1, max = 65535, message = "must be between 1 and 65535 characters")
        String largeInfo,
    @ValidImageUrl(message = "must be a valid image URL") String picture,
    @DecimalMin(value = "0.01", message = "must be a positive number") Double calorieAmount,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String calorieUnit,
    @OnlyOneMainServing Set<@Valid ServingUpdateRequest> servings,
    Set<@Valid NutritionUpdateRequest> nutrients) {}

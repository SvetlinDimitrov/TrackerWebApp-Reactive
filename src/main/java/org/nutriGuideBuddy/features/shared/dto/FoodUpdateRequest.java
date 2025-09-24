package org.nutriGuideBuddy.features.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.annotaions.ValidImageUrl;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;

public record FoodUpdateRequest(
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String name,
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters") String info,
    @Size(min = 1, max = 65535, message = "must be between 1 and 65535 characters")
        String largeInfo,
    @ValidImageUrl(message = "must be a valid image URL") String picture,
    @DecimalMin(value = "0.01", message = "must be a positive number") Double calorieAmount,
    CalorieUnits calorieUnit,
    Set<@Valid ServingUpdateRequest> servings,
    Set<@Valid NutritionUpdateRequest> nutrients) {}

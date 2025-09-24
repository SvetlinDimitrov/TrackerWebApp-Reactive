package org.nutriGuideBuddy.features.custom_food.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.nutriGuideBuddy.features.shared.dto.FoodView;

public record CustomFoodView(@JsonUnwrapped FoodView baseFood) {}

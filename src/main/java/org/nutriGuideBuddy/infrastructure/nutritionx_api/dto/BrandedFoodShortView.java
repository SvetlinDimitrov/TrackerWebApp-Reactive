package org.nutriGuideBuddy.infrastructure.nutritionx_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrandedFoodShortView(
    @JsonProperty("food_name") String foodName,
    @JsonProperty("nix_item_id") String itemId,
    @JsonProperty("brand_name") String brandName) {}

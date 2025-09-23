package org.nutriGuideBuddy.infrastructure.nutritionx_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommandFoodShortenView(
    @JsonProperty("food_name") String foodName, @JsonProperty("tag_name") String tagName) {}

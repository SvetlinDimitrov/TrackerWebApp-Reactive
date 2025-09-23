package org.nutriGuideBuddy.infrastructure.nutritionx_api.dto;

import java.util.List;

public record ListFoodsResponse(
    List<CommandFoodShortenView> common, List<BrandedFoodShortView> branded) {}

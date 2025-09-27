package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import org.mapstruct.*;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.infrastructure.mappers.CustomFoodServingMapper;
import org.nutriGuideBuddy.infrastructure.mappers.MealFoodNutritionMapper;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CustomFoodServingMapper.class, MealFoodNutritionMapper.class})
@DecoratedWith(NutritionxApiFoodMapperDecorator.class)
public interface NutritionxApiFoodMapper {

  @Mapping(target = "name", source = "foodName")
  @Mapping(target = "calorieUnit", constant = "KCAL")
  @Mapping(target = "calorieAmount", source = "nfCalories")
  @Mapping(target = "info", ignore = true)
  @Mapping(target = "largeInfo", ignore = true)
  @Mapping(target = "picture", ignore = true)
  @Mapping(target = "servings", ignore = true)
  @Mapping(target = "nutrients", ignore = true)
  FoodCreateRequest toCreateRequest(FoodItemResponse dto);
}

package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.mapstruct.*;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodProjection;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CustomFoodNutritionMapper.class, CustomFoodServingMapper.class})
public interface CustomFoodMapper {

  CustomFoodView toView(CustomFoodProjection projection);

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "name", source = "entity.name")
  @Mapping(target = "info", source = "entity.info")
  @Mapping(target = "largeInfo", source = "entity.largeInfo")
  @Mapping(target = "picture", source = "entity.picture")
  @Mapping(target = "calorieAmount", source = "entity.calorieAmount")
  @Mapping(target = "calorieUnit", source = "entity.calorieUnit")
  CustomFoodView toView(
      CustomFood entity, Set<ServingView> servings, Set<NutritionView> nutrients);

  CustomFood toEntity(FoodCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(FoodUpdateRequest dto, @MappingTarget CustomFood entity);
}

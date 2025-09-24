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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(CustomFoodDecorator.class)
public interface CustomFoodMapper {

  CustomFoodView toView(CustomFoodProjection projection);

  CustomFoodView toView(
      CustomFood entity, Set<ServingView> servings, Set<NutritionView> nutritions);

  CustomFood toEntity(FoodCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(FoodUpdateRequest dto, @MappingTarget CustomFood entity);
}

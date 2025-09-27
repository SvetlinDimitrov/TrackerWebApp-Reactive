package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodNutrition;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodNutritionProjection;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomFoodNutritionMapper {

  NutritionView toView(CustomFoodNutritionProjection projection);

  NutritionView toView(CustomFoodNutrition entity);

  CustomFoodNutrition toEntity(NutritionCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(NutritionUpdateRequest request, @MappingTarget CustomFoodNutrition entity);
}

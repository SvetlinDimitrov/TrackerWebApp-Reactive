package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.meal.entity.MealFoodServing;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodServingProjection;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MealFoodServingMapper {

  ServingView toView(MealFoodServingProjection projection);

  ServingView toView(MealFoodServing entity);

  MealFoodServing toEntity(ServingCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(ServingUpdateRequest request, @MappingTarget MealFoodServing entity);
}

package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodServing;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodServingProjetion;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomFoodServingMapper {

  ServingView toView(CustomFoodServingProjetion projection);

  ServingView toView(CustomFoodServing entity);

  CustomFoodServing toEntity(ServingCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(ServingUpdateRequest request, @MappingTarget CustomFoodServing entity);
}

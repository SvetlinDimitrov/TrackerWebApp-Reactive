package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.entity.Nutrition;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NutritionMapper {

  NutritionView toView(NutritionProjection projection);

  NutritionView toView(Nutrition entity);

  Nutrition toEntity(NutritionCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(NutritionUpdateRequest request, @MappingTarget Nutrition entity);
}

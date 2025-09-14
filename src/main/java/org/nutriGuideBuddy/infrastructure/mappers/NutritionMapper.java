package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.nutriGuideBuddy.features.food.dto.NutritionView;
import org.nutriGuideBuddy.features.food.repository.projetion.NutritionProjection;

@Mapper(componentModel = "spring")
public interface NutritionMapper {

  NutritionView toView(NutritionProjection projection);
}

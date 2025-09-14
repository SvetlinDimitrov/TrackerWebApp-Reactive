package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.nutriGuideBuddy.features.food.dto.FoodInfoView;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodInfoProjection;

@Mapper(componentModel = "spring")
public interface FoodInfoMapper {

  FoodInfoView toView(FoodInfoProjection projection);
}

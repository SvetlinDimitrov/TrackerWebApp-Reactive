package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nutriGuideBuddy.features.food.dto.FoodShortView;
import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodProjection;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodShortProjection;

@Mapper(
    componentModel = "spring",
    uses = {FoodInfoMapper.class, ServingMapper.class, NutritionMapper.class})
@DecoratedWith(FoodMapperDecorator.class)
public interface FoodMapper {

  @Mapping(target = "mainServing", ignore = true)
  @Mapping(target = "otherServings", source = "serving")
  @Mapping(target = "nutritionList", source = "nutritions")
  FoodView toView(FoodProjection projection);

  FoodShortView toView(FoodShortProjection projection);
}

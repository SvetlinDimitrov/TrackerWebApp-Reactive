package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.mapstruct.*;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodShortView;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodView;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.features.shared.entity.BaseFood;
import org.nutriGuideBuddy.features.shared.repository.projection.FoodProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.FoodShortProjection;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {ServingMapper.class, NutritionMapper.class})
@DecoratedWith(FoodMapperDecorator.class)
public interface FoodMapper {

  FoodView toView(FoodProjection projection);

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "name", source = "entity.name")
  @Mapping(target = "info", source = "entity.info")
  @Mapping(target = "largeInfo", source = "entity.largeInfo")
  @Mapping(target = "picture", source = "entity.picture")
  @Mapping(target = "calorieAmount", source = "entity.calorieAmount")
  @Mapping(target = "calorieUnit", source = "entity.calorieUnit")
  FoodView toView(BaseFood entity, Set<ServingView> servings, Set<NutritionView> nutritions);

  FoodShortView toView(FoodShortProjection projection);

  BaseFood toEntity(FoodCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(FoodUpdateRequest dto, @MappingTarget BaseFood entity);

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

package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.mapstruct.*;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.shared.dto.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(MealFoodDecorator.class)
public interface MealFoodMapper {

  @Mapping(target = "baseFood", ignore = true)
  MealFoodView toView(MealFoodProjection projection);

    @Mapping(target = "baseFood", ignore = true)
  MealFoodView toView(MealFood entity, Set<ServingView> servings, Set<NutritionView> nutritions);

  MealFood toEntity(FoodCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(FoodUpdateRequest dto, @MappingTarget MealFood entity);
}

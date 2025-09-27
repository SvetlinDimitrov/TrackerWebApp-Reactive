package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.mapstruct.*;
import org.nutriGuideBuddy.features.meal.dto.MealFoodShortView;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodShortProjection;
import org.nutriGuideBuddy.features.shared.dto.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {MealFoodServingMapper.class, MealFoodNutritionMapper.class})
public interface MealFoodMapper {

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "mealId", source = "entity.mealId")
  @Mapping(target = "name", source = "entity.name")
  @Mapping(target = "info", source = "entity.info")
  @Mapping(target = "largeInfo", source = "entity.largeInfo")
  @Mapping(target = "picture", source = "entity.picture")
  @Mapping(target = "calorieAmount", source = "entity.calorieAmount")
  @Mapping(target = "calorieUnit", source = "entity.calorieUnit")
  MealFoodView toView(MealFood entity, Set<ServingView> servings, Set<NutritionView> nutrients);

  MealFoodView toView(MealFoodProjection projection);

  MealFoodShortView toShortView(MealFoodShortProjection projection);

  MealFood toEntity(FoodCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(FoodUpdateRequest dto, @MappingTarget MealFood entity);
}

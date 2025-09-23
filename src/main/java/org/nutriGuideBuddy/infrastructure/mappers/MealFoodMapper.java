package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;

import org.mapstruct.*;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodShortProjection;
import org.nutriGuideBuddy.features.shared.dto.FoodConsumedView;
import org.nutriGuideBuddy.features.shared.dto.FoodShortView;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {ServingMapper.class, NutritionMapper.class})
@DecoratedWith(MealFoodMapperDecorator.class)
public interface MealFoodMapper {

  MealFoodView toView(MealFoodProjection projection);

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "name", source = "entity.name")
  @Mapping(target = "info", source = "entity.info")
  @Mapping(target = "largeInfo", source = "entity.largeInfo")
  @Mapping(target = "picture", source = "entity.picture")
  @Mapping(target = "calorieAmount", source = "entity.calorieAmount")
  @Mapping(target = "calorieUnit", source = "entity.calorieUnit")
  MealFoodView toView(MealFood entity, Set<ServingView> servings, Set<NutritionView> nutritions);

  FoodShortView toView(MealFoodShortProjection projection);

  MealFood toEntity(FoodCreateRequest dto);

  FoodConsumedView toConsumedView(MealFoodConsumedProjection projection);

  @Mapping(target = "id", ignore = true)
  void update(MealFoodUpdateRequest dto, @MappingTarget MealFood entity);

  @Mapping(target = "name", source = "foodName")
  @Mapping(target = "calorieUnit", constant = "KCAL")
  @Mapping(target = "calorieAmount", source = "nfCalories")
  @Mapping(target = "info", ignore = true)
  @Mapping(target = "largeInfo", ignore = true)
  @Mapping(target = "picture", ignore = true)
  @Mapping(target = "servings", ignore = true)
  @Mapping(target = "nutrients", ignore = true)
  FoodCreateRequest toMealCreateRequest(FoodItemResponse dto);
}

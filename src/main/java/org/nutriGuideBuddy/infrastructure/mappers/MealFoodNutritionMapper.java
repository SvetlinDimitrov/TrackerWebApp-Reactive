package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;
import org.nutriGuideBuddy.features.meal.entity.MealFoodNutrition;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedDetailedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionProjection;
import org.nutriGuideBuddy.features.shared.dto.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MealFoodNutritionMapper {

  NutritionView toView(MealFoodNutritionProjection projection);

  NutritionView toView(MealFoodNutrition entity);

  MealFoodNutritionConsumedView toConsumedView(MealFoodNutritionConsumedProjection projection);

  MealFoodNutritionConsumedDetailedView toConsumedDetailedView(
      MealFoodNutritionConsumedDetailedProjection projection);

  MealFoodNutrition toEntity(NutritionCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(NutritionUpdateRequest request, @MappingTarget MealFoodNutrition entity);
}

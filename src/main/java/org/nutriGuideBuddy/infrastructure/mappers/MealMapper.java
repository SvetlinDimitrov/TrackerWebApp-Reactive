package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.*;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealDetailedView;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.projection.MealDetailedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {FoodMapper.class})
@DecoratedWith(MealMapperDecorator.class)
public interface MealMapper {

  @Mapping(target = "name", source = "dto.name")
  Meal toEntity(MealCreateRequest dto);

  @Mapping(target = "consumedCalories", ignore = true)
  MealDetailedView toView(MealDetailedProjection projection);

  @Mapping(target = "consumedCalories", ignore = true)
  MealView toView(MealProjection projection);

  void update(MealUpdateRequest dto, @MappingTarget Meal entity);
}

package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.*;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.projection.MealConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {MealFoodMapper.class})
@DecoratedWith(MealMapperDecorator.class)
public interface MealMapper {

  @Mapping(target = "name", source = "dto.name")
  Meal toEntity(MealCreateRequest dto);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updateAt", ignore = true)
  MealView toView(MealProjection projection);

  MealConsumedView toConsumedView(MealConsumedProjection projection);

  void update(MealUpdateRequest dto, @MappingTarget Meal entity);
}

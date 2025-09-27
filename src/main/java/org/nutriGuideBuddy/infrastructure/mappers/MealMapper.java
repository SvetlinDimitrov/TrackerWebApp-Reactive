package org.nutriGuideBuddy.infrastructure.mappers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
public interface MealMapper {

  @Mapping(
      target = "createdAt",
      source = "dto.createdAt",
      qualifiedByName = "localDateToStartOfDayInstant")
  Meal toEntity(MealCreateRequest dto);

  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDate")
  @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToLocalDate")
  MealView toView(MealProjection projection);

  MealConsumedView toConsumedView(MealConsumedProjection projection);

  void update(MealUpdateRequest dto, @MappingTarget Meal entity);

  @Named("localDateToStartOfDayInstant")
  static Instant localDateToStartOfDayInstant(LocalDate date) {
    if (date == null) return null;
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
  }

  @Named("instantToLocalDate")
  static LocalDate instantToLocalDate(Instant instant) {
    if (instant == null) return null;
    return instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }
}

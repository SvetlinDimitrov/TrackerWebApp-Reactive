package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Optional;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class MealMapperDecorator implements MealMapper {

  private MealMapper delegate;

  @Autowired
  public void setDelegate(MealMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public Meal toEntity(MealCreateRequest dto) {
    var entity = delegate.toEntity(dto);
    Optional.ofNullable(dto.createdAt())
        .ifPresent(
            date ->
                entity.setCreatedAt(
                    date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
    return entity;
  }

  @Override
  public MealView toView(MealProjection projection) {
    MealView view = delegate.toView(projection);
    return new MealView(
        view.id(),
        view.userId(),
        view.name(),
        projection.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        projection.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        view.totalCalories(),
        view.foods());
  }
}

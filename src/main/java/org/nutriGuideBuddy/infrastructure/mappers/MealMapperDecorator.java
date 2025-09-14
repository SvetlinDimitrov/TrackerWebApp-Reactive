package org.nutriGuideBuddy.infrastructure.mappers;

import java.math.BigDecimal;
import org.nutriGuideBuddy.features.food.repository.projetion.CalorieProjection;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.dto.MealDetailedView;
import org.nutriGuideBuddy.features.meal.repository.projection.MealDetailedProjection;
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
  public MealDetailedView toView(MealDetailedProjection projection) {
    MealDetailedView view = delegate.toView(projection);
    return new MealDetailedView(
        view.id(),
        view.name(),
        projection.getCalories().stream()
            .map(CalorieProjection::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add),
        view.foods());
  }

  @Override
  public MealView toView(MealProjection projection) {
    MealView view = delegate.toView(projection);
    return new MealView(
        view.id(),
        view.userId(),
        view.name(),
        projection.getCalories().stream()
            .map(CalorieProjection::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add),
        view.foods());
  }
}

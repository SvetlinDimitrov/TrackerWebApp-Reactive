package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class MealFoodDecorator implements MealFoodMapper {

  private FoodMapper foodMapper;
  private MealFoodMapper delegate;

  @Autowired
  public void setDelegate(FoodMapper foodMapper) {
    this.foodMapper = foodMapper;
  }

  @Autowired
  public void setDelegate(MealFoodMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public MealFoodView toView(MealFoodProjection projection) {
    MealFoodView mealFoodView = delegate.toView(projection);
    FoodView baseFoodView = foodMapper.toView(projection);
    return new MealFoodView(baseFoodView, mealFoodView.mealId());
  }

  @Override
  public MealFoodView toView(
      MealFood entity, Set<ServingView> servings, Set<NutritionView> nutritions) {
    MealFoodView mealFoodView = delegate.toView(entity, servings, nutritions);
    FoodView baseFoodView = foodMapper.toView(entity, servings, nutritions);
    return new MealFoodView(baseFoodView, mealFoodView.mealId());
  }
}

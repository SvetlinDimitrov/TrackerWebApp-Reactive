package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Set;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodProjection;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CustomFoodDecorator implements CustomFoodMapper {

  private FoodMapper foodMapper;

  //  private CustomFoodMapper delegate;

  @Autowired
  public void setDelegate(FoodMapper foodMapper) {
    this.foodMapper = foodMapper;
  }

  @Override
  public CustomFoodView toView(CustomFoodProjection projection) {
    var baseFoodView = foodMapper.toView(projection);
    return new CustomFoodView(baseFoodView);
  }

  @Override
  public CustomFoodView toView(
      CustomFood entity, Set<ServingView> servings, Set<NutritionView> nutritions) {
    var baseFoodView = foodMapper.toView(entity, servings, nutritions);
    return new CustomFoodView(baseFoodView);
  }
}

package org.nutriGuideBuddy.features.food.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.nutriGuideBuddy.features.food.entity.FoodEntity;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FoodView(
    String id,
    String name,
    FoodInfoView additionalInfo,
    ServingView mainServing,
    List<ServingView> otherServings,
    CalorieView calorie,
    List<NutritionView> nutritionList) {

  public static FoodView toView(
      FoodEntity entity,
      List<NutritionView> nutritionList,
      CalorieView calorie,
      List<ServingView> servings,
      ServingView mainServing,
      FoodInfoView info) {
    return new FoodView(
        entity.getId(), entity.getName(), info, mainServing, servings, calorie, nutritionList);
  }
}

package org.nutriGuideBuddy.features.food.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.nutriGuideBuddy.features.food.entity.Food;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FoodView(
    Long id,
    String name,
    FoodInfoView foodInfo,
    ServingView mainServing,
    List<ServingView> otherServings,
    CalorieView calorie,
    List<NutritionView> nutritionList) {

  public static FoodView toView(
      Food entity,
      List<NutritionView> nutritionList,
      CalorieView calorie,
      List<ServingView> servings,
      ServingView mainServing,
      FoodInfoView info) {
    return new FoodView(
        entity.getId(), entity.getName(), info, mainServing, servings, calorie, nutritionList);
  }
}

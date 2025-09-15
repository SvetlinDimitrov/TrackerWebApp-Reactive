package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.Optional;
import org.nutriGuideBuddy.features.food.dto.FoodInfoView;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItem;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.Photo;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.Tags;

public class FoodInfoMapperUtils {

  public static FoodInfoView generateFoodInfo(FoodItem foodItem) {
    return Optional.ofNullable(foodItem.getBrandName())
        .map(
            brandName ->
                new FoodInfoView(
                    brandName, foodItem.getNfIngredientStatement(), getPicture(foodItem)))
        .orElse(
            new FoodInfoView(
                Optional.ofNullable(foodItem.getTags()).map(Tags::getItem).orElse(null),
                foodItem.getNfIngredientStatement(),
                getPicture(foodItem)));
  }

  private static String getPicture(FoodItem foodItem) {
    return Optional.ofNullable(foodItem.getPhoto()).map(Photo::getThumb).orElse(null);
  }
}

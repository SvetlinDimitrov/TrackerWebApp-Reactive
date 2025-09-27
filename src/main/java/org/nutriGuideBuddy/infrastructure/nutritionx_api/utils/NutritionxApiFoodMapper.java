package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.Optional;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;
import org.springframework.stereotype.Component;

@Component
public final class NutritionxApiFoodMapper {

  /**
   * Builds a FoodCreateRequest from a Nutritionix FoodItemResponse. - name: dto.foodName -
   * calorieUnit: KCAL - calorieAmount: dto.nfCalories - info: "Brand: <brandName>" if present -
   * largeInfo: null (intentionally) - picture: dto.photo().thumb if present - servings:
   * AllowedServingMapper.map(dto) - nutrients: AllowedNutrientMapper.map(dto)
   */
  public static FoodCreateRequest toCreateRequest(FoodItemResponse dto) {
    if (dto == null) {
      return null;
    }

    String name = dto.foodName();
    Double calorieAmount = dto.nfCalories();

    var info =
        Optional.ofNullable(blankToNull(dto.brandName())).map(b -> "Brand: " + b).orElse(null);

    var picture =
        Optional.ofNullable(dto.photo()).map(FoodItemResponse.Photo::thumb).orElse(null);

    return new FoodCreateRequest(
        name,
        info,
        null,
        picture,
        calorieAmount,
        CalorieUnits.KCAL,
        AllowedServingMapper.map(dto),
        AllowedNutrientMapper.map(dto));
  }

  private static String blankToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}

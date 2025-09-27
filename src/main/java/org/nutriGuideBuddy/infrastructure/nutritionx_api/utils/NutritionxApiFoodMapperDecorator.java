package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.Optional;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class NutritionxApiFoodMapperDecorator implements NutritionxApiFoodMapper {

  private NutritionxApiFoodMapper delegate;

  @Autowired
  public void setDelegate(NutritionxApiFoodMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public FoodCreateRequest toCreateRequest(FoodItemResponse dto) {
    var mealCreateRequest = delegate.toCreateRequest(dto);
    String info =
        Optional.ofNullable(dto.brandName())
            .filter(b -> !b.isBlank())
            .map(b -> "Brand: " + b)
            .orElse(null);

    String picture =
        Optional.ofNullable(dto.photo()).map(FoodItemResponse.Photo::thumb).orElse(null);
    return new FoodCreateRequest(
        mealCreateRequest.name(),
        info,
        mealCreateRequest.largeInfo(),
        picture,
        mealCreateRequest.calorieAmount(),
        mealCreateRequest.calorieUnit(),
        AllowedServingMapper.map(dto),
        AllowedNutrientMapper.map(dto));
  }
}

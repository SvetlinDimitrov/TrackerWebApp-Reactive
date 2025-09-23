package org.nutriGuideBuddy.infrastructure.mappers;

import java.util.Optional;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.utils.AllowedNutrientMapper;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.utils.AllowedServingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class MealFoodMapperDecorator implements MealFoodMapper {

  private MealFoodMapper delegate;

  @Autowired
  public void setDelegate(MealFoodMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public FoodCreateRequest toMealCreateRequest(FoodItemResponse dto) {
    var mealCreateRequest = delegate.toMealCreateRequest(dto);
    String info =
        Optional.ofNullable(dto.brandName())
            .filter(b -> !b.isBlank())
            .map(b -> "Brand: " + b)
            .orElse(null);

    String picture =
        Optional.ofNullable(dto.photo())
            .map(FoodItemResponse.Photo::thumb)
            .orElse(null);
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

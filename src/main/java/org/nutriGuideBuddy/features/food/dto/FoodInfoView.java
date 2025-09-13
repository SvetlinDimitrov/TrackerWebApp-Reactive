package org.nutriGuideBuddy.features.food.dto;

import org.nutriGuideBuddy.features.food.entity.FoodInfoEntity;

public record FoodInfoView(String info, String largeInfo, String picture) {

  public static FoodInfoView toView(FoodInfoEntity foodInfoView) {
    return new FoodInfoView(
        foodInfoView.getInfo(), foodInfoView.getLargeInfo(), foodInfoView.getPicture());
  }
}

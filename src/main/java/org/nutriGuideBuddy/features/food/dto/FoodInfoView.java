package org.nutriGuideBuddy.features.food.dto;

import org.nutriGuideBuddy.features.food.entity.FoodInfo;

public record FoodInfoView(String info, String largeInfo, String picture) {

  public static FoodInfoView toView(FoodInfo foodInfoView) {
    return new FoodInfoView(
        foodInfoView.getInfo(), foodInfoView.getLargeInfo(), foodInfoView.getPicture());
  }
}

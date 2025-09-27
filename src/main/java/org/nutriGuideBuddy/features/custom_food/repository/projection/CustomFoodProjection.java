package org.nutriGuideBuddy.features.custom_food.repository.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CustomFoodProjection {

  private Long id;
  private String name;
  private String info;
  private String largeInfo;
  private String picture;
  private Double calorieAmount;
  private String calorieUnit;
  private List<CustomFoodServingProjection> servings = new ArrayList<>();
  private List<CustomFoodNutritionProjection> nutrients = new ArrayList<>();
}

package org.nutriGuideBuddy.features.meal.repository.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodProjection {

  private Long id;
  private Long mealId;
  private String name;
  private String info;
  private String largeInfo;
  private String picture;
  private Double calorieAmount;
  private String calorieUnit;
  private List<MealFoodServingProjection> servings = new ArrayList<>();
  private List<MealFoodNutritionProjection> nutrients = new ArrayList<>();
}

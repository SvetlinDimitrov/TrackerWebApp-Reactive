package org.nutriGuideBuddy.features.meal.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodNutritionConsumedProjection {

  private Long mealId;
  private String mealName;
  private Long foodId;
  private String foodName;
  private Double amount;
}

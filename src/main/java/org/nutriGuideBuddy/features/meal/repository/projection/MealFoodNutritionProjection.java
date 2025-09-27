package org.nutriGuideBuddy.features.meal.repository.projection;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodNutritionProjection {

  private Long id;
  private String name;
  private String unit;
  private Double amount;
}

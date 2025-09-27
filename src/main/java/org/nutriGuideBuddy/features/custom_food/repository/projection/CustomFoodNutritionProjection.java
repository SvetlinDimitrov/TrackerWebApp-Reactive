package org.nutriGuideBuddy.features.custom_food.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFoodNutritionProjection {

  private Long id;
  private String name;
  private String unit;
  private Double amount;
}

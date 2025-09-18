package org.nutriGuideBuddy.features.meal.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodConsumedProjection {

  private Long id;
  private String name;
  private Double amount;
}

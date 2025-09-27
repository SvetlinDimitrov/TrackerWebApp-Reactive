package org.nutriGuideBuddy.features.meal.repository.projection;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodNutritionConsumedDetailedProjection {

  private Long id;
  private String name;
  private String unit;
  private Set<MealFoodNutritionConsumedProjection> consumed;
}

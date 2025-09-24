package org.nutriGuideBuddy.features.meal.repository.projection;

import lombok.*;
import org.nutriGuideBuddy.features.shared.repository.projection.FoodProjection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodProjection extends FoodProjection {

  private Long mealId;
}

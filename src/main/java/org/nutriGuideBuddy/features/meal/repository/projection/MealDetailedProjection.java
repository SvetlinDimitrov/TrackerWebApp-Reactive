package org.nutriGuideBuddy.features.meal.repository.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.food.repository.projetion.CalorieProjection;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodProjection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDetailedProjection {
  private Long id;
  private String name;
  private List<CalorieProjection> calories = new ArrayList<>();
  private List<FoodProjection> foods = new ArrayList<>();
}

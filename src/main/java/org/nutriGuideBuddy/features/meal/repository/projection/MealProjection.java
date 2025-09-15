package org.nutriGuideBuddy.features.meal.repository.projection;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.food.repository.projetion.CalorieProjection;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodShortProjection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealProjection {
  private Long id;
  private Long userId;
  private String name;
  private Instant createdAt;
  private Instant updatedAt;
  private List<CalorieProjection> calories = new ArrayList<>();
  private List<FoodShortProjection> foods = new ArrayList<>();
}

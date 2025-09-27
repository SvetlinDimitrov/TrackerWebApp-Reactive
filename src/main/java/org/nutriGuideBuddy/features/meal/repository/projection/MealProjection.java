package org.nutriGuideBuddy.features.meal.repository.projection;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealProjection {
  private Long id;
  private Long userId;
  private String name;
  private Instant createdAt;
  private Instant updatedAt;
  private Double totalCalories;
  private List<MealFoodShortProjection> foods = new ArrayList<>();
}

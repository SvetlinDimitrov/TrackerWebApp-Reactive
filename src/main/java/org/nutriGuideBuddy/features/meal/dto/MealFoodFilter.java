package org.nutriGuideBuddy.features.meal.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodFilter {

  private String name;
  private Double minCalorieAmount;
  private Double maxCalorieAmount;
  private Set<String> idsIn;
  private Set<String> idsNotIn;
  private CustomPageableMealFood pageable;
}

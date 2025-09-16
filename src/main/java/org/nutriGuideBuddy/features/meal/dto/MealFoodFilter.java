package org.nutriGuideBuddy.features.meal.dto;

import java.util.Set;

import jakarta.validation.Valid;
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
  private Set<Long> idsIn;
  private Set<Long> idsNotIn;
  private @Valid CustomPageableMealFood pageable;
}

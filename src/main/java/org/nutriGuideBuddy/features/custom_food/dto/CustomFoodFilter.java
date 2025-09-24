package org.nutriGuideBuddy.features.custom_food.dto;

import jakarta.validation.Valid;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFoodFilter {

  private String name;
  private Double minCalorieAmount;
  private Double maxCalorieAmount;
  private Set<Long> idsIn;
  private Set<Long> idsNotIn;
  private @Valid CustomPageableCustomFood pageable;
}

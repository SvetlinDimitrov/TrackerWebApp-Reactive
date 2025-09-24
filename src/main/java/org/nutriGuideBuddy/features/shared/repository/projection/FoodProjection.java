package org.nutriGuideBuddy.features.shared.repository.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodProjection {
  private Long id;
  private String name;
  private String info;
  private String largeInfo;
  private String picture;
  private Double calorieAmount;
  private String calorieUnit;
  private List<ServingProjection> servings = new ArrayList<>();
  private List<NutritionProjection> nutritions = new ArrayList<>();
}

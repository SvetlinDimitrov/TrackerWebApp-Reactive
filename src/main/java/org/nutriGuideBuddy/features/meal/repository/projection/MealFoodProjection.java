package org.nutriGuideBuddy.features.meal.repository.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.ServingProjection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodProjection {
  private Long id;
  private String name;
  private String info;
  private String largeInfo;
  private String picture;
  private Double calorieAmount;
  private String calorieUnit;
  private List<ServingProjection> serving = new ArrayList<>();
  private List<NutritionProjection> nutritions = new ArrayList<>();
}

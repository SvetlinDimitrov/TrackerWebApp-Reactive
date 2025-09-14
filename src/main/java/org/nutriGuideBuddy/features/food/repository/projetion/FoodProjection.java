package org.nutriGuideBuddy.features.food.repository.projetion;

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
  private CalorieProjection calorie;
  private FoodInfoProjection foodInfo;
  private List<ServingProjection> serving = new ArrayList<>();
  private List<NutritionProjection> nutritions = new ArrayList<>();
}

package org.nutriGuideBuddy.features.food.repository.projetion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodInfoProjection {
  private Long id;
  private String info;
  private String largeInfo;
  private String picture;
}

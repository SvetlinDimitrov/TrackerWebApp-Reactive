package org.nutriGuideBuddy.features.shared.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionProjection {

  private String name;
  private String unit;
  private Double amount;
}

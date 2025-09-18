package org.nutriGuideBuddy.features.shared.repository.projection;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionConsumedDetailedProjection {

  private Long id;
  private String name;
  private String unit;
  private Set<NutritionConsumedProjection> consumed;
}

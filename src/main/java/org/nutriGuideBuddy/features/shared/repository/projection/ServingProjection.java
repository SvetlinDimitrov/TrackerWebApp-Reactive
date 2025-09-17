package org.nutriGuideBuddy.features.shared.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServingProjection {

  private Long id;
  private Double amount;
  private ServingMetric metric;
  private Boolean main;
}

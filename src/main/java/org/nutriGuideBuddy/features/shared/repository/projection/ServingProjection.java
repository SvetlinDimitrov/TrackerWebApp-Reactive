package org.nutriGuideBuddy.features.shared.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServingProjection {

  private Long id;
  private Double amount;
  private Double gramsTotal;
  private String metric;
  private Boolean main;
}

package org.nutriGuideBuddy.features.food.repository.projetion;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServingProjection {

  private Long id;
  private BigDecimal amount;
  private BigDecimal servingWeight;
  private String metric;
  private Boolean main = false;
  private Long foodId;
}

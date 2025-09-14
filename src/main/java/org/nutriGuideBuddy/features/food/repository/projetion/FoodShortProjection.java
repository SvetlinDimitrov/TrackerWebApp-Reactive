package org.nutriGuideBuddy.features.food.repository.projetion;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodShortProjection {

  private Long id;
  private String name;
  private BigDecimal calories;
}

package org.nutriGuideBuddy.features.custom_food.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFoodServingProjetion {

  private Long id;
  private Double amount;
  private Double gramsTotal;
  private String metric;
  private Boolean main;
}

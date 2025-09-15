package org.nutriGuideBuddy.infrastructure.nutritionx_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FullNutrient {

  @JsonProperty("attr_id")
  private Integer tag;

  @JsonProperty("value")
  private BigDecimal value;
}

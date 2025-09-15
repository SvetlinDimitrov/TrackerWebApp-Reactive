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
public class AltMeasures {

  @JsonProperty("serving_weight")
  private BigDecimal servingWeight;

  private String measure;
  private BigDecimal seq;
  private BigDecimal qty;
}

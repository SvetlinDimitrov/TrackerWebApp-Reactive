package org.nutriGuideBuddy.features.food.dto;

import java.math.BigDecimal;
import org.nutriGuideBuddy.features.food.entity.Serving;

public record ServingView(BigDecimal amount, BigDecimal servingWeight, String metric) {
  public static ServingView toView(Serving entity) {
    return new ServingView(entity.getAmount(), entity.getServingWeight(), entity.getMetric());
  }
}

package org.nutriGuideBuddy.features.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ShortenFood {

  private String id;
  private String name;
  private BigDecimal calories;
}

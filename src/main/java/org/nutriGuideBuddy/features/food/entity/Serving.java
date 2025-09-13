package org.nutriGuideBuddy.features.food.entity;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servings")
public class Serving {

  @Id private String id = UUID.randomUUID().toString();

  @Column("amount")
  private BigDecimal amount;

  @Column("serving_weight")
  private BigDecimal servingWeight;

  @Column("metric")
  private String metric;

  @Column("main")
  private Boolean main = false;

  @Column("food_id")
  private String foodId;
}

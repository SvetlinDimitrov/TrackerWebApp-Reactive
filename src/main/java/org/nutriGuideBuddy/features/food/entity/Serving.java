package org.nutriGuideBuddy.features.food.entity;

import java.math.BigDecimal;
import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servings")
public class Serving extends BaseEntity {

  @Column("amount")
  private BigDecimal amount;

  @Column("serving_weight")
  private BigDecimal servingWeight;

  @Column("metric")
  private String metric;

  @Column("main")
  private Boolean main = false;

  @Column("food_id")
  private Long foodId;
}

package org.nutriGuideBuddy.features.food.entity;

import java.math.BigDecimal;
import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.AuditableEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nutritions")
public class Nutrition extends AuditableEntity {

  @Column("name")
  private String name;

  @Column("unit")
  private String unit;

  @Column("amount")
  private BigDecimal amount;

  @Column("food_id")
  private Long foodId;

  @Column("user_id")
  private Long userId;
}

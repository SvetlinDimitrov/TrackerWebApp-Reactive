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
@Table(name = "calories")
public class Calorie extends AuditableEntity {

  @Column("amount")
  private BigDecimal amount;

  @Column("unit")
  private String unit;

  @Column("meal_id")
  private Long mealId;

  @Column("food_id")
  private Long foodId;

  @Column("user_id")
  private Long userId;
}

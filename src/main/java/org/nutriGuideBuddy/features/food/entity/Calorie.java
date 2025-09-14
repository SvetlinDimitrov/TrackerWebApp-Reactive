package org.nutriGuideBuddy.features.food.entity;

import java.math.BigDecimal;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calories")
public class Calorie {

  @Id private Long id;

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

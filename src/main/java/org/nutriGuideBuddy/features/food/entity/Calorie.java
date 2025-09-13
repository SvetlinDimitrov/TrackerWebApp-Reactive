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
@Table(name = "calories")
public class Calorie {

  @Id private String id = UUID.randomUUID().toString();

  @Column("amount")
  private BigDecimal amount;

  @Column("unit")
  private String unit;

  @Column("meal_id")
  private String mealId;

  @Column("food_id")
  private String foodId;

  @Column("user_id")
  private String userId;
}

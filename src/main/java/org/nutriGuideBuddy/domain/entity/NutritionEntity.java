package org.nutriGuideBuddy.domain.entity;

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
@Table(name = "nutritions")
public class NutritionEntity {

  @Id private String id = UUID.randomUUID().toString();

  @Column("name")
  private String name;

  @Column("unit")
  private String unit;

  @Column("amount")
  private BigDecimal amount;

  @Column("food_id")
  private String foodId;

  @Column("user_id")
  private String userId;
}

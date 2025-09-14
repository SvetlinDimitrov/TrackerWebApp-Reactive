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
@Table(name = "nutritions")
public class Nutrition {

  @Id private Long id;

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

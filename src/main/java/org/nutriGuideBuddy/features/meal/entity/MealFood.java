package org.nutriGuideBuddy.features.meal.entity;

import java.time.Instant;
import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseFood;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_foods")
public class MealFood extends BaseFood {

  @Column("meal_id")
  private Long mealId;

  @Column("created_at")
  private Instant createdAt = Instant.now();

  @Column("updated_at")
  private Instant updatedAt = Instant.now();
}

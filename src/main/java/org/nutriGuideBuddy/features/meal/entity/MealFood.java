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
@Table("meal_foods")
public class MealFood extends BaseFood {

  @Column("created_at")
  private Instant createdAt;

  @Column("updated_at")
  private Instant updatedAt;

  @Column("meal_id")
  private Long mealId;

  @Column("user_id")
  private Long userId;
}

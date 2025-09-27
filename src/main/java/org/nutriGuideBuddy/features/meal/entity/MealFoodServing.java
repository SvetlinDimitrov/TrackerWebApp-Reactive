package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseServing;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table("meal_food_serving")
public class MealFoodServing extends BaseServing {

  @Column("food_id")
  private Long foodId;
}

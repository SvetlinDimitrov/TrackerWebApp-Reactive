package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("meal_foods_servings")
public class MealFoodServing {

  @Column("meal_food_id")
  private Long mealFoodId;

  @Column("serving_id")
  private Long servingId;
}

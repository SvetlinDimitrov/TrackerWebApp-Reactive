package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("meal_foods_nutritions")
public class MealFoodNutrition extends BaseEntity {

  @Column("meal_food_id")
  private Long mealFoodId;

  @Column("nutrition_id")
  private Long nutritionId;
}

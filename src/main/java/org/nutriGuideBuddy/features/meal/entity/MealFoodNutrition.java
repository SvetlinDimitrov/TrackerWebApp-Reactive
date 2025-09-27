package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseNutrition;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table("meal_food_nutrition")
public class MealFoodNutrition extends BaseNutrition {

  @Column("food_id")
  private Long foodId;
}

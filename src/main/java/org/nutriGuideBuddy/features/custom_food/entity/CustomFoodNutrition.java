package org.nutriGuideBuddy.features.custom_food.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table("custom_food_nutritions")
public class CustomFoodNutrition {

  @Column("custom_food_id")
  private Long customFoodId;

  @Column("nutrition_id")
  private Long nutritionId;
}

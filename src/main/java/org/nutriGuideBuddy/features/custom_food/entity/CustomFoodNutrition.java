package org.nutriGuideBuddy.features.custom_food.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseNutrition;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table("custom_food_nutritions")
public class CustomFoodNutrition extends BaseNutrition {

  @Column("food_id")
  private Long foodId;
}

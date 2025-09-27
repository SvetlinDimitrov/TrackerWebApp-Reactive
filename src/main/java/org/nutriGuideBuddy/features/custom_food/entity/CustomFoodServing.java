package org.nutriGuideBuddy.features.custom_food.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseServing;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table("custom_food_servings")
public class CustomFoodServing extends BaseServing {

  @Column("food_id")
  private Long foodId;
}

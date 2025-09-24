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
@Table("custom_food_servings")
public class CustomFoodServing {

  @Column("custom_food_id")
  private Long customFoodId;

  @Column("serving_id")
  private Long servingId;
}

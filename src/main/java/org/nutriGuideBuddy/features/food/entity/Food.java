package org.nutriGuideBuddy.features.food.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inserted_foods")
public class Food extends BaseEntity {

  @Column("name")
  private String name;

  @Column("meal_id")
  private Long mealId;

  @Column("user_id")
  private Long userId;
}

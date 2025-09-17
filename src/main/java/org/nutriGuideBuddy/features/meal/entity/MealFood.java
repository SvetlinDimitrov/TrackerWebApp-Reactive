package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.AuditableEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_foods")
public class MealFood extends AuditableEntity {

  @Column("name")
  private String name;

  @Column("info")
  private String info;

  @Column("large_info")
  private String largeInfo;

  @Column("picture")
  private String picture;

  @Column("calorie_amount")
  private Double calorieAmount;

  @Column("calorie_unit")
  private String calorieUnit;

  @Column("meal_id")
  private Long mealId;
}

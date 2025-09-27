package org.nutriGuideBuddy.features.shared.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseFood extends BaseEntity {
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
  private CalorieUnits calorieUnit;
}

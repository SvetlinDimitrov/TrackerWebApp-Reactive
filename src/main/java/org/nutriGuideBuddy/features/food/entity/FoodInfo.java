package org.nutriGuideBuddy.features.food.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "info_foods")
public class FoodInfo extends BaseEntity {

  @Column("info")
  private String info;

  @Column("large_info")
  private String largeInfo;

  @Column("picture")
  private String picture;

  @Column("food_id")
  private Long foodId;
}

package org.nutriGuideBuddy.features.food.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "info_foods")
public class FoodInfo {

  @Id private String id = UUID.randomUUID().toString();

  @Column("info")
  private String info;

  @Column("large_info")
  private String largeInfo;

  @Column("picture")
  private String picture;

  @Column("food_id")
  private String foodId;
}

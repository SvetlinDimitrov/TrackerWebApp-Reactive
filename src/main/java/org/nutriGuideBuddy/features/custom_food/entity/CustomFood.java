package org.nutriGuideBuddy.features.custom_food.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseFood;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "custom_food")
public class CustomFood extends BaseFood {

  @Column("user_id")
  private Long userId;
}

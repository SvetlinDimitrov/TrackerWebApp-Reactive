package org.nutriGuideBuddy.features.shared.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseNutrition extends BaseEntity {

  @Column("name")
  private String name;

  @Column("unit")
  private String unit;

  @Column("amount")
  private Double amount;
}

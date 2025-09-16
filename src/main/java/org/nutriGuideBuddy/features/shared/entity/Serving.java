package org.nutriGuideBuddy.features.shared.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servings")
public class Serving extends BaseEntity {

  @Column("amount")
  private Double amount;

  @Column("serving_weight")
  private Double servingWeight;

  @Column("metric")
  private String metric;

  @Column("main")
  private Boolean main = false;
}

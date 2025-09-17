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
@Table(name = "nutritions")
public class Nutrition extends AuditableEntity {

  @Column("name")
  private String name;

  @Column("unit")
  private String unit;

  @Column("amount")
  private Double amount;
}

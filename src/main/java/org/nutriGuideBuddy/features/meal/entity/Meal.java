package org.nutriGuideBuddy.features.meal.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.AuditableEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "meals")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Meal extends AuditableEntity {

  @Column("name")
  private String name;

  @Column("user_id")
  private Long userId;
}

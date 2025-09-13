package org.nutriGuideBuddy.features.meal.entity;

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
@Table(name = "meals")
public class MealEntity {

  @Id private String id = UUID.randomUUID().toString();

  @Column("name")
  private String name;

  @Column("user_id")
  private String userId;
}

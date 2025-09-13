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
@Table(name = "inserted_foods")
public class Food {

  @Id private String id = UUID.randomUUID().toString();

  @Column("name")
  private String name;

  @Column("meal_id")
  private String mealId;

  @Column("user_id")
  private String userId;
}

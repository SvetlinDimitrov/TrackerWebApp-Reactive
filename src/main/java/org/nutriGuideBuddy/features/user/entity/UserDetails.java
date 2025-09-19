package org.nutriGuideBuddy.features.user.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class UserDetails extends BaseEntity {

  @Column("kilograms")
  private Double kilograms;

  @Column("height")
  private Double height;

  @Column("age")
  private Integer age;

  @Column("workout_state")
  private WorkoutState workoutState;

  @Column("gender")
  private Gender gender;

  @Column("goal")
  private Goals goal;

  @Column("diet")
  private DietType diet;

  @Column("nutrition_authority")
  private NutritionAuthority nutritionAuthority;

  @Column("user_id")
  private Long userId;

  public boolean isFullyCreated() {
    return kilograms != null
        && height != null
        && age != null
        && workoutState != null
        && goal != null
        && diet != null
        && gender != null
        && nutritionAuthority != null
        && userId != null;
  }
}

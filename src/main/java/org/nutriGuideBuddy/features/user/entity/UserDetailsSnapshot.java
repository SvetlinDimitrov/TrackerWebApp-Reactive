package org.nutriGuideBuddy.features.user.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.AuditableEntity;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details_snapshots")
public class UserDetailsSnapshot extends AuditableEntity {

  @Column("user_id")
  private Long userId;

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
}

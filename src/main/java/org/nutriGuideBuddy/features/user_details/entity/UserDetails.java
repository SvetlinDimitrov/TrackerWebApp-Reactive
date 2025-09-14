package org.nutriGuideBuddy.features.user_details.entity;

import java.math.BigDecimal;
import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.features.user_details.enums.Gender;
import org.nutriGuideBuddy.features.user_details.enums.WorkoutState;
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
  private BigDecimal kilograms;

  @Column("height")
  private BigDecimal height;

  @Column("age")
  private Integer age;

  @Column("workout_state")
  private WorkoutState workoutState;

  @Column("gender")
  private Gender gender;

  @Column("user_id")
  private Long userId;
}

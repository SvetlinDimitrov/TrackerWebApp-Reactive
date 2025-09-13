package org.nutriGuideBuddy.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.domain.enums.Gender;
import org.nutriGuideBuddy.domain.enums.UserRoles;
import org.nutriGuideBuddy.domain.enums.WorkoutState;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class UserDetails {

  @Id private String id = UUID.randomUUID().toString();

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
  private String userId;

  public String getRole() {
    return (age != null
            && height != null
            && kilograms != null
            && gender != null
            && workoutState != null)
        ? UserRoles.FULLY_REGISTERED.name()
        : UserRoles.NOT_FULLY_REGISTERED.name();
  }
}

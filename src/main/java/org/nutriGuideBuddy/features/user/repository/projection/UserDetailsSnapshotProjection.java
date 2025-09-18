package org.nutriGuideBuddy.features.user.repository.projection;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.enums.DuetTypes;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsSnapshotProjection {

  private Long id;
  private Double kilograms;
  private Double height;
  private Integer age;
  private WorkoutState workoutState;
  private Gender gender;
  private Goals goal;
  private DuetTypes duet;
  private Long userId;
  private Instant createdAt;
  private Instant updatedAt;
}

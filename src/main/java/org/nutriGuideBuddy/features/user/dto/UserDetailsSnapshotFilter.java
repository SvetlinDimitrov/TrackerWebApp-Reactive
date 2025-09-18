package org.nutriGuideBuddy.features.user.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsSnapshotFilter {

  private LocalDate to;
  private LocalDate from;
  private Set<Long> idsIn;
  private Set<Long> idsNotIn;
  private CustomPageableUserDetailsSnapshot pageable;
}

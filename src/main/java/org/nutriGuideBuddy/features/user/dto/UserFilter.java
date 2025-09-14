package org.nutriGuideBuddy.features.user.dto;

import jakarta.validation.Valid;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.user.annotations.ValidUserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
  private String username;
  private String email;
  @ValidUserRole private String role;
  private Set<String> idsIn;
  private Set<String> idsNotIn;
  @Valid private CustomPageableUser pageable = new CustomPageableUser();
}

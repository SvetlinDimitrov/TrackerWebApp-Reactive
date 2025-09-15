package org.nutriGuideBuddy.features.meal.dto;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealFilter {
  private String name;
  private LocalDate createdAt;
  private Set<String> idsIn;
  private Set<String> idsNotIn;
  @Valid private CustomPageableMeal pageable = new CustomPageableMeal();
}

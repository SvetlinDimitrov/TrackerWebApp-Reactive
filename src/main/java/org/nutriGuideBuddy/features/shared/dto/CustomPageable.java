package org.nutriGuideBuddy.features.shared.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.features.shared.annotaions.ValidSortDirection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageable {
  private Integer pageNumber = 0;
  private Integer pageSize = 25;
  @ValidSortDirection private Map<String, String> sort = new LinkedHashMap<>();
}

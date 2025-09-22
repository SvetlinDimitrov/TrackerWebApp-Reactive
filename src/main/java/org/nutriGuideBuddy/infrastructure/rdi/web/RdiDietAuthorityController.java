package org.nutriGuideBuddy.infrastructure.rdi.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.service.RdiDietAuthorityServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/rdi/diet-authorities")
@RequiredArgsConstructor
public class RdiDietAuthorityController {

  private final RdiDietAuthorityServiceImpl service;

  @GetMapping
  public Mono<List<String>> get() {
    return service.getAllDietTypes();
  }

  @GetMapping("/{diet}/{authority}/nutrients")
  public Mono<List<String>> getNutritions(
      @PathVariable DietType diet, @PathVariable NutritionAuthority authority) {
    return service.getNutrientNames(diet, authority);
  }

  @GetMapping("/{diet}/{authority}/nutrients/detailed")
  public Mono<Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      getNutritionsDetailed(
          @PathVariable DietType diet, @PathVariable NutritionAuthority authority) {
    return service.getDetailedRequirements(diet, authority);
  }
}

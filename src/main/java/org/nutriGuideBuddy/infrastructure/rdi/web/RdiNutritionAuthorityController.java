package org.nutriGuideBuddy.infrastructure.rdi.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.service.RdiNutritionAuthorityServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/rdi/nutrition-authorities")
@RequiredArgsConstructor
public class RdiNutritionAuthorityController {

  private final RdiNutritionAuthorityServiceImpl service;

  @GetMapping
  public Mono<List<String>> getNames() {
    return service.getAllNames();
  }

  @GetMapping("/{authority}/nutrients")
  public Mono<List<String>> getNutritionNames(@PathVariable NutritionAuthority authority) {
    return service.getNutrientNames(authority);
  }

  @GetMapping("/{authority}/nutrients/detailed")
  public Mono<Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      getNutritionDetailed(@PathVariable NutritionAuthority authority) {
    return service.getDetailedRequirements(authority);
  }
}

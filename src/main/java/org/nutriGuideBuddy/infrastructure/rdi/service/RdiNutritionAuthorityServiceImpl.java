package org.nutriGuideBuddy.infrastructure.rdi.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.utils.NutrientAuthorityStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RdiNutritionAuthorityServiceImpl {

  private final NutrientAuthorityStore nutrientAuthorityStore;

  public Mono<List<String>> getAllNames() {
    return Flux.fromArray(NutritionAuthority.values()).map(Enum::name).sort().collectList();
  }

  public Mono<List<String>> getNutrientNames(NutritionAuthority authority) {
    return Flux.fromIterable(nutrientAuthorityStore.getRequirements(authority).keySet())
        .map(Enum::name)
        .sort()
        .collectList();
  }

  public Mono<Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      getDetailedRequirements(NutritionAuthority authority) {
    return Mono.just(nutrientAuthorityStore.getRequirements(authority));
  }
}

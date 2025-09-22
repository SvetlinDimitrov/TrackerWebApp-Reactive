package org.nutriGuideBuddy.infrastructure.rdi.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.utils.DietAuthorityStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RdiDietAuthorityServiceImpl {

  private final DietAuthorityStore dietAuthorityStore;

  public Mono<List<String>> getAllDietTypes() {
    return Flux.fromArray(DietType.values()).map(Enum::name).sort().collectList();
  }

  public Mono<List<String>> getNutrientNames(DietType diet, NutritionAuthority authority) {
    return Flux.fromIterable(dietAuthorityStore.getRequirements(diet, authority).keySet())
        .map(Enum::name)
        .sort()
        .collectList();
  }

  public Mono<Map<JsonAllowedNutrients, Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>>
      getDetailedRequirements(DietType diet, NutritionAuthority authority) {
    return Mono.just(dietAuthorityStore.getRequirements(diet, authority));
  }
}

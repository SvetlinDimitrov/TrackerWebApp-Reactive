package org.nutriGuideBuddy.features.meal.service;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealFoodNutritionService {

  Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long foodId);

  Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long foodId);

  Mono<Void> deleteNutritionsForFoodId(Long foodId);

  Mono<Void> deleteNutritionsForFoodIdIn(Set<Long> foodIds);
}

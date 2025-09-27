package org.nutriGuideBuddy.features.meal.service;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import reactor.core.publisher.Flux;

public interface MealFoodServingService {

  Flux<ServingView> create(Set<ServingCreateRequest> requests, Long foodId);

  Flux<ServingView> update(Set<ServingUpdateRequest> requests, Long foodId);
}

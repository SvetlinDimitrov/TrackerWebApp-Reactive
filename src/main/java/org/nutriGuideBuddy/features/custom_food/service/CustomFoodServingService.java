package org.nutriGuideBuddy.features.custom_food.service;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import reactor.core.publisher.Flux;

public interface CustomFoodServingService {

  Flux<ServingView> create(Set<ServingCreateRequest> requests, Long customFoodId);

  Flux<ServingView> update(Set<ServingUpdateRequest> requests, Long customFoodId);
}

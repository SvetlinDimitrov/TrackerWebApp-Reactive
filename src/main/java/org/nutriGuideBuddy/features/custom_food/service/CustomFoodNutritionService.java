package org.nutriGuideBuddy.features.custom_food.service;

import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import reactor.core.publisher.Flux;

public interface CustomFoodNutritionService {

  Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long customFoodId);

  Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long customFoodId);
}

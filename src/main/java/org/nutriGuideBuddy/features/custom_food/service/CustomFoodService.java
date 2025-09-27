package org.nutriGuideBuddy.features.custom_food.service;

import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomFoodService {

  Mono<CustomFoodView> create(FoodCreateRequest dto);

  Mono<CustomFoodView> create(FoodCreateRequest dto, Long userId);

  Mono<Long> countByFilter(CustomFoodFilter filter);

  Flux<CustomFoodView> getAll(CustomFoodFilter filter);

  Mono<CustomFoodView> getById(Long id);

  Mono<CustomFoodView> update(FoodUpdateRequest dto, Long id);

  Mono<Void> delete(Long id);

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  Mono<Long> countByUserId(Long userId);
}

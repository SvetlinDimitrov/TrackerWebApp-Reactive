package org.nutriGuideBuddy.features.custom_food.repository;

import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomFoodCustomRepository {

  Mono<CustomFoodProjection> findById(Long customFoodId);

  Flux<CustomFoodProjection> findAllByUserIdAndFilter(Long userId, CustomFoodFilter filter);

  Mono<Long> countByUserIdAndFilter(Long userId, CustomFoodFilter filter);
}

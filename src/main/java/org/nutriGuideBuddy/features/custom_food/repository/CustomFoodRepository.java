package org.nutriGuideBuddy.features.custom_food.repository;

import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CustomFoodRepository extends R2dbcRepository<CustomFood, Long> {

  Mono<CustomFood> findByIdAndUserId(Long id, Long userId);

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  Mono<Boolean> existsByNameAndUserId(String name, Long userId);

  Mono<Long> countByUserId(Long userId);

  Mono<Boolean> existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);
}

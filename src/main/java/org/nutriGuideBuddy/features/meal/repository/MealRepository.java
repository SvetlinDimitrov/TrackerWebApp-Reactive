package org.nutriGuideBuddy.features.meal.repository;

import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealRepository extends R2dbcRepository<Meal, Long> {

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  Mono<Boolean> existsByNameAndUserId(String name, Long userId);

  Mono<Boolean> existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);

  Flux<Meal> findAllByUserId(Long userId);
}

package org.nutriGuideBuddy.features.food.repository;

import org.nutriGuideBuddy.features.food.entity.Food;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FoodRepository extends R2dbcRepository<Food, Long> {

  Mono<Food> findByIdAndMealIdAndUserId(Long id, Long mealId, Long userId);

  Flux<Food> findAllByMealId(Long mealId);
}

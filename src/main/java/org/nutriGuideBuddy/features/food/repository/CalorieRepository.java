package org.nutriGuideBuddy.features.food.repository;

import org.nutriGuideBuddy.features.food.entity.Calorie;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CalorieRepository extends R2dbcRepository<Calorie, Long> {

  Mono<Calorie> findByFoodIdAndMealId(Long foodId, Long mealId);

  Flux<Calorie> findAllByUserId(Long userId);
}

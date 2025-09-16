package org.nutriGuideBuddy.features.meal.repository;

import java.util.Set;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealFoodRepository extends R2dbcRepository<MealFood, Long> {
  Mono<Boolean> existsByIdAndMealId(Long id, Long mealId);

  Mono<Void> deleteByIdAndMealId(Long id, Long mealId);

  Mono<MealFood> findByIdAndMealId(Long id, Long mealId);

  Flux<MealFood> findByMealIdIn(Set<Long> mealIds);
}

package org.nutriGuideBuddy.features.meal.repository;

import org.nutriGuideBuddy.features.meal.entity.MealFoodServing;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MealFoodServingRepository extends ReactiveCrudRepository<MealFoodServing, Long> {

  Flux<MealFoodServing> findAllByFoodId(Long foodId);
}

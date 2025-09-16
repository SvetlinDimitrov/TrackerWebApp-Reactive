package org.nutriGuideBuddy.features.meal.repository;

import java.util.Set;
import org.nutriGuideBuddy.features.meal.entity.MealFoodServing;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MealFoodServingRepository extends ReactiveCrudRepository<MealFoodServing, Long> {
  Flux<MealFoodServing> findByMealFoodId(Long mealFoodId);

  Flux<MealFoodServing> findByMealFoodIdIn(Set<Long> mealFoodIds);
}

package org.nutriGuideBuddy.features.meal.repository;

import java.util.Set;
import org.nutriGuideBuddy.features.meal.entity.MealFoodNutrition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MealFoodNutritionRepository
    extends ReactiveCrudRepository<MealFoodNutrition, Long> {
  Flux<MealFoodNutrition> findByMealFoodId(Long mealFoodId);

  Flux<MealFoodNutrition> findByMealFoodIdIn(Set<Long> mealFoodIds);
}

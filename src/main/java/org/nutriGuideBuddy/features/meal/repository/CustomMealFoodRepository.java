package org.nutriGuideBuddy.features.meal.repository;

import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomMealFoodRepository {

  Mono<MealFoodProjection> findById(Long mealFoodId);

  Flux<MealFoodProjection> findAllByMealIdAndFilter(Long mealId, MealFoodFilter filter);

  Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter);
}

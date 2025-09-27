package org.nutriGuideBuddy.features.meal.service;

import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealFoodService {

  Mono<MealFoodView> create(FoodCreateRequest dto, Long mealId);

  Mono<MealFoodView> create(FoodCreateRequest dto, Long mealId, Long userId);

  Flux<MealFoodView> getAll(Long mealId, MealFoodFilter filter);

  Mono<MealFoodView> getById(Long foodId);

  Mono<Void> delete(Long id, Long mealId);

  Mono<MealFoodView> update(FoodUpdateRequest dto, Long foodId, Long mealId);

  Mono<Boolean> existsByIdAndMealId(Long id, Long mealId);

  Mono<MealFood> findByIdAndMealIdOrThrow(Long id, Long mealId);

  Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter);
}

package org.nutriGuideBuddy.features.meal.service;

import java.time.LocalDate;
import java.util.Set;
import org.nutriGuideBuddy.features.meal.dto.MealFoodCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.dto.MealFoodUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealFoodService {

  Mono<MealFoodView> create(MealFoodCreateRequest dto, Long mealId);

  Mono<MealFoodView> create(MealFoodCreateRequest dto, Long mealId , Long userId);

  Flux<MealFoodView> getAll(Long mealId, MealFoodFilter filter);

  Mono<MealFoodView> getById(Long foodId);

  Mono<Void> delete(Long id, Long mealId);

  Mono<Void> deleteAllByMealIdsIn(Set<Long> mealIds);

  Mono<MealFoodView> update(MealFoodUpdateRequest dto, Long foodId, Long mealId);

  Mono<Boolean> existsByIdAndMealId(Long id, Long mealId);

  Mono<MealFood> findByIdAndMealIdOrThrow(Long id, Long mealId);

  Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter);

  Mono<Double> sumConsumedCaloriesByUserIdAndDate(Long userId, LocalDate date);
}

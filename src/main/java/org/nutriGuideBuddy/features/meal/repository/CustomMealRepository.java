package org.nutriGuideBuddy.features.meal.repository;

import java.time.LocalDate;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomMealRepository {

  Flux<MealProjection> findAllWithFoodDetailsByFilterAndUserId(MealFilter filter, Long userId);

  Mono<Long> countByFilterAndUserId(MealFilter filter, Long userId);

  Mono<MealProjection> findById(Long mealId);

  Flux<MealConsumedProjection> findMealsConsumtionWithFoodsByUserIdAndDate(
      Long userId, LocalDate date);
}

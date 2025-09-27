package org.nutriGuideBuddy.features.meal.repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedDetailedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedProjection;
import reactor.core.publisher.Mono;

public interface MealFoodNutritionCustomRepository {

  /**
   * Aggregates all nutrients consumed by a user on a given date.
   *
   * @param userId the user id
   * @param date the target date
   * @return map of nutrient name -> aggregated projection
   */
  Mono<Map<String, MealFoodNutritionConsumedDetailedProjection>> findUserDailyNutrition(
      Long userId, LocalDate date);

  Mono<Map<LocalDate, Set<MealFoodNutritionConsumedProjection>>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate);
}

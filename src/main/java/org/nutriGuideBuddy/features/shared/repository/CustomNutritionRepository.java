package org.nutriGuideBuddy.features.shared.repository;

import java.time.LocalDate;
import java.util.Map;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;
import reactor.core.publisher.Mono;

public interface CustomNutritionRepository {

  /**
   * Aggregates all nutrients consumed by a user on a given date.
   *
   * @param userId the user id
   * @param date the target date
   * @return map of nutrient name -> aggregated projection
   */
  Mono<Map<String, NutritionProjection>> findUserDailyNutrition(Long userId, LocalDate date);

  Mono<Map<LocalDate, Double>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate);
}

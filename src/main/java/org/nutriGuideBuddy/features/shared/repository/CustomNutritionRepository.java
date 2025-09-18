package org.nutriGuideBuddy.features.shared.repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionConsumedDetailedProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionConsumedProjection;
import reactor.core.publisher.Mono;

public interface CustomNutritionRepository {

  /**
   * Aggregates all nutrients consumed by a user on a given date.
   *
   * @param userId the user id
   * @param date the target date
   * @return map of nutrient name -> aggregated projection
   */
  Mono<Map<String, NutritionConsumedDetailedProjection>> findUserDailyNutrition(
      Long userId, LocalDate date);

  Mono<Map<LocalDate, Set<NutritionConsumedProjection>>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate);
}

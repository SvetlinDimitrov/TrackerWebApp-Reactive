package org.nutriGuideBuddy.features.meal.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MealFoodNutritionService {

  Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long foodId);

  Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long foodId);

  Mono<Map<String, MealFoodNutritionConsumedDetailedView>> findUserDailyNutrition(
      Long userId, LocalDate date);

  Mono<Map<LocalDate, Set<MealFoodNutritionConsumedView>>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate);
}

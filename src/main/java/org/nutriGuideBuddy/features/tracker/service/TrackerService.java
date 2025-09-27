package org.nutriGuideBuddy.features.tracker.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.features.tracker.dto.CalorieRequest;
import org.nutriGuideBuddy.features.tracker.dto.NutritionRequest;
import org.nutriGuideBuddy.features.tracker.dto.TrackerRequest;
import org.nutriGuideBuddy.features.tracker.dto.TrackerView;
import reactor.core.publisher.Mono;

public interface TrackerService {

  Mono<TrackerView> get(TrackerRequest dto, Long userId);

  Mono<Map<LocalDate, Set<MealFoodNutritionConsumedView>>> getNutritionForRange(
      NutritionRequest request);

  Mono<Map<LocalDate, Set<MealConsumedView>>> getCaloriesInRange(CalorieRequest request);
}

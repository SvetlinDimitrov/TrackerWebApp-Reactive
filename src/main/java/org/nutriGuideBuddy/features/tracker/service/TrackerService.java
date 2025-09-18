package org.nutriGuideBuddy.features.tracker.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealService;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedView;
import org.nutriGuideBuddy.features.shared.service.NutritionServiceImpl;
import org.nutriGuideBuddy.features.tracker.dto.*;
import org.nutriGuideBuddy.features.tracker.utils.CalorieCalculator;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.RdiProviderFactory;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrackerService {

  private final UserDetailsSnapshotService userDetailsSnapshotService;
  private final NutritionServiceImpl nutritionService;
  private final MealService mealService;

  public Mono<TrackerView> get(TrackerRequest dto, Long userId) {
    LocalDate localDate =
        Optional.ofNullable(dto).map(TrackerRequest::date).orElseGet(LocalDate::now);

    Instant dateInstant = localDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

    return userDetailsSnapshotService
        .findLatestByUserIdAndDate(userId, dateInstant)
        .flatMap(
            snapshot ->
                Mono.zip(
                        CalorieCalculator.calculateDailyCalories(snapshot, snapshot.goal()),
                        mealService.getAllConsumedByDateAndUserId(userId, localDate).collectList(),
                        nutritionService.findUserDailyNutrition(userId, localDate))
                    .map(
                        tuple -> {
                          double calorieGoal = tuple.getT1();
                          var consumedList = tuple.getT2();
                          Map<String, NutritionConsumedDetailedView> consumedMap = tuple.getT3();

                          Gender gender = snapshot.gender();
                          int age = snapshot.age();

                          var provider = RdiProviderFactory.getProvider(snapshot.duet());

                          Set<NutritionIntakeView> nutrients =
                              provider.getSupportedNutrients().stream()
                                  .map(
                                      nutrient -> {
                                        Set<NutritionConsumedView> consumed =
                                            consumedMap.containsKey(nutrient.getNutrientName())
                                                ? consumedMap
                                                    .get(nutrient.getNutrientName())
                                                    .consumed()
                                                : Set.of();
                                        double recommended =
                                            provider.getRecommended(nutrient, gender, age);
                                        return new NutritionIntakeView(
                                            nutrient.getNutrientName(),
                                            consumed,
                                            recommended,
                                            nutrient.getNutrientUnit());
                                      })
                                  .collect(Collectors.toSet());

                          return new TrackerView(calorieGoal, consumedList, nutrients);
                        }));
  }

  public Mono<Map<LocalDate, Set<NutritionConsumedView>>> getNutritionForRange(
      NutritionRequest request) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                nutritionService.findUserNutritionDailyAmountsView(
                    userId, request.name(), request.startDate(), request.endDate()));
  }

  public Mono<Map<LocalDate, Set<MealConsumedView>>> getCaloriesInRange(CalorieRequest request) {
    LocalDate today = LocalDate.now();

    LocalDate startDate;
    LocalDate endDate;

    if (request == null || (request.startDate() == null && request.endDate() == null)) {
      startDate = today;
      endDate = today;
    } else if (request.startDate() == null) {
      startDate = request.endDate();
      endDate = request.endDate();
    } else if (request.endDate() == null) {
      startDate = request.startDate();
      endDate = request.startDate();
    } else {
      startDate = request.startDate();
      endDate = request.endDate();
    }

    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mealService.getCaloriesInRange(startDate, endDate, userId));
  }
}

package org.nutriGuideBuddy.features.tracker.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealService;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedView;
import org.nutriGuideBuddy.features.shared.service.NutritionServiceImpl;
import org.nutriGuideBuddy.features.tracker.dto.*;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
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
    // Convert LocalDate from dto into an Instant at end of day
    Instant dateInstant = dto.date().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

    return userDetailsSnapshotService
        .findLatestByUserIdAndDate(userId, dateInstant)
        .flatMap(
            snapshot ->
                Mono.zip(
                        CalorieCalculator.calculateDailyCalories(
                            snapshot, Goals.valueOf(dto.goal())),
                        mealService.getAllConsumedByDateAndUserId(userId, dto.date()).collectList(),
                        nutritionService.findUserDailyNutrition(userId, dto.date()))
                    .map(
                        tuple -> {
                          double calorieGoal = tuple.getT1();
                          var consumedList = tuple.getT2();
                          Map<String, NutritionConsumedDetailedView> consumedMap = tuple.getT3();

                          Gender gender = snapshot.gender();
                          int age = snapshot.age();

                          var provider =
                              RdiProviderFactory.getProvider(RdiProviderFactory.DietType.STANDARD);

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
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                mealService.getCaloriesInRange(request.startDate(), request.endDate(), userId));
  }
}

package org.nutriGuideBuddy.features.tracker.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;
import org.nutriGuideBuddy.features.meal.service.MealFoodNutritionService;
import org.nutriGuideBuddy.features.meal.service.MealService;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.tracker.dto.*;
import org.nutriGuideBuddy.features.tracker.utils.CalorieCalculator;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonAllowedNutrients;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonNutrientRdiRange;
import org.nutriGuideBuddy.infrastructure.rdi.dto.JsonPopulationGroup;
import org.nutriGuideBuddy.infrastructure.rdi.utils.NutrientRequirementFactory;
import org.nutriGuideBuddy.infrastructure.rdi.utils.RdiFinder;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrackerServiceImpl implements TrackerService {

  private final UserDetailsSnapshotService userDetailsSnapshotService;
  private final MealFoodNutritionService nutritionService;
  private final MealService mealService;
  private final NutrientRequirementFactory requirementFactory;

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
                          Map<String, MealFoodNutritionConsumedDetailedView> consumedMap =
                              tuple.getT3();

                          var gender = snapshot.gender();
                          int age = snapshot.age();

                          Map<
                                  JsonAllowedNutrients,
                                  Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>>
                              requirements =
                                  requirementFactory.build(
                                      snapshot.nutritionAuthority(), snapshot.diet());

                          var nutrientsSet =
                              Arrays.stream(AllowedNutrients.values())
                                  .map(
                                      nutrient -> {
                                        var jsonNutrient =
                                            JsonAllowedNutrients.valueOf(nutrient.name());

                                        Map<JsonPopulationGroup, Set<JsonNutrientRdiRange>>
                                            groupMap =
                                                requirements.getOrDefault(
                                                    jsonNutrient, Collections.emptyMap());

                                        var rdiRange =
                                            RdiFinder.findMatch(
                                                groupMap,
                                                gender,
                                                age,
                                                calorieGoal,
                                                snapshot.kilograms());

                                        Set<MealFoodNutritionConsumedView> consumed =
                                            consumedMap.containsKey(nutrient.getNutrientName())
                                                ? consumedMap
                                                    .get(nutrient.getNutrientName())
                                                    .consumed()
                                                : Set.of();

                                        return new NutritionIntakeView(
                                            nutrient.getNutrientName(),
                                            consumed,
                                            Optional.ofNullable(rdiRange)
                                                .flatMap(JsonNutrientRdiRange::rdiMin)
                                                .orElse(null),
                                            Optional.ofNullable(rdiRange)
                                                .flatMap(JsonNutrientRdiRange::rdiMax)
                                                .orElse(null),
                                            nutrient.getNutrientUnit());
                                      })
                                  .collect(Collectors.toSet());

                          return new TrackerView(calorieGoal, consumedList, nutrientsSet);
                        }));
  }

  public Mono<Map<LocalDate, Set<MealFoodNutritionConsumedView>>> getNutritionForRange(
      NutritionRequest request) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                nutritionService.findUserNutritionDailyAmounts(
                    userId, request.name(), request.startDate(), request.endDate()));
  }

  public Mono<Map<LocalDate, Set<MealConsumedView>>> getCaloriesInRange(CalorieRequest request) {
    var today = LocalDate.now();

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

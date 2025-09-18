package org.nutriGuideBuddy.features.tracker.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.features.meal.service.MealService;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedView;
import org.nutriGuideBuddy.features.shared.service.NutritionServiceImpl;
import org.nutriGuideBuddy.features.tracker.dto.*;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.tracker.utils.CalorieCalculator;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.MacronutrientRdiData;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.MineralRdiData;
import org.nutriGuideBuddy.features.tracker.utils.rdi_nutrients.VitaminRdiData;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrackerService {

  private final UserService userService;
  private final MealFoodService mealFoodService;
  private final NutritionServiceImpl nutritionService;
  private final MealService mealService;

  public Mono<TrackerView> get(TrackerRequest dto, Long userId) {
    return userService
        .getByIdWithDetails(userId)
        .flatMap(
            user ->
                Mono.zip(
                        CalorieCalculator.calculateDailyCalories(
                            user.details(), Goals.valueOf(dto.goal())),
                        mealService.getAllConsumedByDateAndUserId(userId, dto.date()).collectList(),
                        nutritionService.findUserDailyNutrition(userId, dto.date()))
                    .map(
                        tuple -> {
                          double calorieGoal = tuple.getT1();
                          var consumedList = tuple.getT2();
                          Map<String, NutritionConsumedDetailedView> consumedMap = tuple.getT3();

                          Gender gender = user.details().gender();
                          int age = user.details().age();

                          var vitamins = buildVitaminIntakes(consumedMap, gender, age);
                          var minerals = buildMineralIntakes(consumedMap, gender, age);
                          var macros = buildMacronutrientIntakes(consumedMap, gender, age);

                          return new TrackerView(
                              calorieGoal, consumedList, vitamins, minerals, macros);
                        }));
  }

  private Set<NutritionIntakeView> buildVitaminIntakes(
      Map<String, NutritionConsumedDetailedView> consumedMap, Gender gender, int age) {
    return VitaminRdiData.getSupportedNutrients().stream()
        .map(
            nutrient -> {
              Set<NutritionConsumedView> consumed =
                  consumedMap.containsKey(nutrient.getNutrientName())
                      ? consumedMap.get(nutrient.getNutrientName()).consumed()
                      : Set.of();
              double recommended = VitaminRdiData.getRecommended(nutrient, gender, age);
              return new NutritionIntakeView(
                  nutrient.getNutrientName(), consumed, recommended, nutrient.getNutrientUnit());
            })
        .collect(Collectors.toSet());
  }

  private Set<NutritionIntakeView> buildMineralIntakes(
      Map<String, NutritionConsumedDetailedView> consumedMap, Gender gender, int age) {
    return MineralRdiData.getSupportedNutrients().stream()
        .map(
            nutrient -> {
              Set<NutritionConsumedView> consumed =
                  consumedMap.containsKey(nutrient.getNutrientName())
                      ? consumedMap.get(nutrient.getNutrientName()).consumed()
                      : Set.of();
              double recommended = MineralRdiData.getRecommended(nutrient, gender, age);
              return new NutritionIntakeView(
                  nutrient.getNutrientName(), consumed, recommended, nutrient.getNutrientUnit());
            })
        .collect(Collectors.toSet());
  }

  private Set<NutritionIntakeView> buildMacronutrientIntakes(
      Map<String, NutritionConsumedDetailedView> consumedMap, Gender gender, int age) {
    return MacronutrientRdiData.getSupportedNutrients().stream()
        .map(
            nutrient -> {
              Set<NutritionConsumedView> consumed =
                  consumedMap.containsKey(nutrient.getNutrientName())
                      ? consumedMap.get(nutrient.getNutrientName()).consumed()
                      : Set.of();
              double recommended = MacronutrientRdiData.getRecommended(nutrient, gender, age);
              return new NutritionIntakeView(
                  nutrient.getNutrientName(), consumed, recommended, nutrient.getNutrientUnit());
            })
        .collect(Collectors.toSet());
  }

  public Mono<Map<LocalDate, Set<NutritionConsumedView>>> getNutritionForRange(
      NutritionRequest request) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                nutritionService.findUserNutritionDailyAmountsView(
                    userId, request.name(), request.startDate(), request.endDate()));
  }

  public Mono<Map<LocalDate, Double>> getCaloriesInRange(CalorieRequest request) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                mealFoodService.getCaloriesInRange(request.startDate(), request.endDate(), userId));
  }
}

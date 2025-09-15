package org.nutriGuideBuddy.features.record.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.dto.NutritionIntakeView;
import org.nutriGuideBuddy.features.food.entity.Calorie;
import org.nutriGuideBuddy.features.food.repository.CalorieRepository;
import org.nutriGuideBuddy.features.food.repository.NutrientRepository;
import org.nutriGuideBuddy.features.record.dto.CreateRecord;
import org.nutriGuideBuddy.features.record.dto.DistributedMacros;
import org.nutriGuideBuddy.features.record.dto.NutritionView;
import org.nutriGuideBuddy.features.record.dto.RecordView;
import org.nutriGuideBuddy.features.record.enums.Goals;
import org.nutriGuideBuddy.features.record.utils.*;
import org.nutriGuideBuddy.features.record.utils.BMRCalc;
import org.nutriGuideBuddy.features.record.utils.DailyCaloriesCalculator;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.nutriGuideBuddy.features.user_details.repository.UserDetailsRepository;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RecordService {

  private final NutrientRepository nutrientRepository;
  private final UserDetailsRepository userDetailsRepository;
  private final CalorieRepository calorieRepository;

  public Mono<RecordView> viewRecord(CreateRecord dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userDetailsRepository::findByUserId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
        .flatMap(
            details ->
                Mono.zip(
                    Mono.just(details),
                    CustomNutritionsValidator.validate(dto.nutritions())
                        .switchIfEmpty(Mono.just(List.of())),
                    fetchRecordViewData(details, dto.goal())))
        .flatMap(
            data ->
                Mono.zip(
                    setNutritionViews(data.getT1(), data.getT3(), dto.distributedMacros()),
                    Mono.just(data.getT2())))
        .flatMap(data -> customizeRecordView(data.getT1(), data.getT2()));
  }

  private Mono<RecordView> fetchRecordViewData(UserDetails details, Goals goal) {
    return Mono.just(details)
        .flatMap(
            userDetails -> {
              RecordView view = new RecordView();
              view.setDailyCaloriesToConsume(
                  DailyCaloriesCalculator.getCaloriesPerDay(
                          BMRCalc.calculateBMR(
                              userDetails.getGender(),
                              BigDecimal.valueOf(userDetails.getKilograms()),
                              BigDecimal.valueOf(userDetails.getHeight()),
                              userDetails.getAge()),
                          userDetails.getWorkoutState(),
                          Optional.ofNullable(goal).orElse(Goals.MaintainWeight))
                      .setScale(0, RoundingMode.DOWN));
              return Mono.zip(Mono.just(userDetails), Mono.just(view));
            })
        .flatMap(
            data ->
                calorieRepository
                    .findAllByUserId(data.getT1().getUserId())
                    .collectList()
                    .map(
                        list ->
                            list.stream()
                                .map(Calorie::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .setScale(0, RoundingMode.DOWN))
                    .map(
                        dailyConsumedCalories -> {
                          data.getT2().setDailyCaloriesConsumed(dailyConsumedCalories);
                          return data.getT2();
                        }));
  }

  private Mono<RecordView> setNutritionViews(
      UserDetails details, RecordView view, DistributedMacros distributedMacros) {

    return DistributedMacrosValidator.validate(distributedMacros)
        .flatMap(
            validatedDistribution ->
                Mono.zip(
                    Mono.just(view),
                    Mono.just(new HashMap<String, NutritionIntakeView>())
                        .map(
                            map -> {
                              MineralCreator.fillMinerals(
                                  map, details.getGender(), details.getAge());
                              VitaminCreator.fillVitamins(
                                  map, details.getGender(), details.getAge());
                              MacronutrientCreator.fillMacros(
                                  map,
                                  view.getDailyCaloriesToConsume(),
                                  details.getGender(),
                                  validatedDistribution,
                                  details.getAge());
                              return map;
                            }),
                    nutrientRepository.findAllByUserId(details.getUserId()).collectList()))
        .map(
            data -> {
              Map<String, NutritionIntakeView> nutritions = data.getT2();
              data.getT3()
                  .forEach(
                      entity -> {
                        NutritionIntakeView intakeView = nutritions.get(entity.getName());
                        intakeView.setDailyConsumed(
                            intakeView
                                .getDailyConsumed()
                                .add(entity.getAmount())
                                .setScale(0, RoundingMode.DOWN));
                      });
              setVitaminIntakes(data.getT1(), nutritions);
              setMineralsIntakes(data.getT1(), nutritions);
              setMacrosIntakes(data.getT1(), nutritions);
              return data.getT1();
            });
  }

  private void setVitaminIntakes(RecordView view, Map<String, NutritionIntakeView> intakeViewMap) {
    view.setVitaminIntake(
        intakeViewMap.values().stream()
            .filter(
                nutritionIntakeView ->
                    VitaminCreator.allAllowedVitamins.contains(nutritionIntakeView.getName()))
            .toList());
  }

  private void setMineralsIntakes(RecordView view, Map<String, NutritionIntakeView> intakeViewMap) {
    view.setMineralIntakes(
        intakeViewMap.values().stream()
            .filter(
                nutritionIntakeView ->
                    MineralCreator.allAllowedMinerals.contains(nutritionIntakeView.getName()))
            .toList());
  }

  private void setMacrosIntakes(RecordView view, Map<String, NutritionIntakeView> intakeViewMap) {
    view.setMacroIntakes(
        intakeViewMap.values().stream()
            .filter(
                nutritionIntakeView ->
                    MacronutrientCreator.allAllowedMacros.contains(nutritionIntakeView.getName()))
            .toList());
  }

  private Mono<RecordView> customizeRecordView(
      RecordView record, List<NutritionView> NutritionViews) {

    if (NutritionViews == null || NutritionViews.isEmpty()) {
      return Mono.just(record);
    }

    Map<String, BigDecimal> customIntakeMap =
        NutritionViews.stream()
            .collect(Collectors.toMap(NutritionView::name, NutritionView::recommendedIntake));

    return Mono.fromCallable(
        () -> {
          record
              .getMineralIntakes()
              .forEach(
                  intake -> {
                    if (customIntakeMap.containsKey(intake.getName())) {
                      intake.setRecommendedIntake(customIntakeMap.get(intake.getName()));
                    }
                  });
          record
              .getMacroIntakes()
              .forEach(
                  intake -> {
                    if (customIntakeMap.containsKey(intake.getName())) {
                      intake.setRecommendedIntake(customIntakeMap.get(intake.getName()));
                    }
                  });
          record
              .getVitaminIntake()
              .forEach(
                  intake -> {
                    if (customIntakeMap.containsKey(intake.getName())) {
                      intake.setRecommendedIntake(customIntakeMap.get(intake.getName()));
                    }
                  });
          return record;
        });
  }
}

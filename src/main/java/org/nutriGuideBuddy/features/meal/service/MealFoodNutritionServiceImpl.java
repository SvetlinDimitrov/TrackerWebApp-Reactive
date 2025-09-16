package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NUTRITION_NOT_BELONG_TO_MEAL_FOOD;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.entity.MealFoodNutrition;
import org.nutriGuideBuddy.features.meal.repository.MealFoodNutritionRepository;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.service.NutritionServiceImpl;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.NutritionMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealFoodNutritionServiceImpl {

  private final MealFoodNutritionRepository repository;
  private final NutritionServiceImpl nutritionService;
  private final NutritionMapper mapper;

  public Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long foodId) {
    return nutritionService
        .create(requests)
        .collectList()
        .flatMapMany(
            nutritions -> {
              var mealFoodNutritions =
                  nutritions.stream()
                      .map(nutrition -> new MealFoodNutrition(foodId, nutrition.getId()))
                      .toList();
              return repository
                  .saveAll(mealFoodNutritions)
                  .thenMany(Flux.fromIterable(nutritions).map(mapper::toView));
            });
  }

  //TODO :: THIS WONT RETURN ALL NUTRITIONS IF ONLY FEW ARE UPDATED
  public Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    Set<Long> ids = requests.stream().map(NutritionUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findByMealFoodId(foodId)
        .map(MealFoodNutrition::getNutritionId)
        .collectList()
        .flatMapMany(
            existingNutritionIds -> {
              boolean allBelong = new HashSet<>(existingNutritionIds).containsAll(ids);
              if (!allBelong) {
                return Flux.error(
                    new BadRequestException(
                        String.format(NUTRITION_NOT_BELONG_TO_MEAL_FOOD, ids, foodId)));
              }
              return nutritionService.update(requests).map(mapper::toView);
            });
  }

  public Mono<Void> deleteNutritionsForFoodId(Long foodId) {
    return repository
        .findByMealFoodId(foodId)
        .collectList()
        .flatMap(
            mealFoodNutritions -> {
              if (mealFoodNutritions.isEmpty()) {
                return Mono.empty();
              }

              Set<Long> nutritionIds =
                  mealFoodNutritions.stream()
                      .map(MealFoodNutrition::getNutritionId)
                      .collect(Collectors.toSet());

              return nutritionService.delete(nutritionIds);
            });
  }

  public Mono<Void> deleteNutritionsForFoodIdIn(Set<Long> foodIds) {
    if (foodIds == null || foodIds.isEmpty()) {
      return Mono.empty();
    }

    return repository
        .findByMealFoodIdIn(foodIds)
        .collectList()
        .flatMap(
            mealFoodNutritions -> {
              if (mealFoodNutritions.isEmpty()) {
                return Mono.empty();
              }

              Set<Long> nutritionIds =
                  mealFoodNutritions.stream()
                      .map(MealFoodNutrition::getNutritionId)
                      .collect(Collectors.toSet());

              return nutritionService.delete(nutritionIds);
            });
  }
}

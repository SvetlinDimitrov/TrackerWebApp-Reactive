package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.SERVING_NOT_BELONG_TO_MEAL_FOOD;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.entity.MealFoodServing;
import org.nutriGuideBuddy.features.meal.repository.MealFoodServingRepository;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.features.shared.service.ServingServiceImpl;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.ServingMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealFoodServingServiceImpl {

  private final MealFoodServingRepository repository;
  private final ServingServiceImpl servingService;
  private final ServingMapper mapper;

  public Flux<ServingView> create(Set<ServingCreateRequest> requests, Long foodId) {
    return servingService
        .create(requests)
        .collectList()
        .flatMapMany(
            servings -> {
              var mealFoodServings =
                  servings.stream()
                      .map(serving -> new MealFoodServing(foodId, serving.getId()))
                      .toList();
              return repository
                  .saveAll(mealFoodServings)
                  .thenMany(Flux.fromIterable(servings).map(mapper::toView));
            });
  }

  public Flux<ServingView> update(Set<ServingUpdateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    Set<Long> ids = requests.stream().map(ServingUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findByMealFoodId(foodId)
        .map(MealFoodServing::getServingId)
        .collectList()
        .flatMapMany(
            existingServingIds -> {
              boolean allBelong = new HashSet<>(existingServingIds).containsAll(ids);
              if (!allBelong) {
                return Flux.error(
                    new BadRequestException(
                        String.format(SERVING_NOT_BELONG_TO_MEAL_FOOD, ids, foodId)));
              }
              return servingService.update(requests).map(mapper::toView);
            });
  }

  public Mono<Void> deleteServingsForFoodId(Long foodId) {
    return repository
        .findByMealFoodId(foodId)
        .collectList()
        .flatMap(
            mealFoodServings -> {
              if (mealFoodServings.isEmpty()) {
                return Mono.empty();
              }

              Set<Long> servingIds =
                  mealFoodServings.stream()
                      .map(MealFoodServing::getServingId)
                      .collect(Collectors.toSet());

              return servingService.delete(servingIds);
            });
  }

  public Mono<Void> deleteServingsForFoodIdIn(Set<Long> foodIds) {
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

              Set<Long> servingIds =
                  mealFoodNutritions.stream()
                      .map(MealFoodServing::getServingId)
                      .collect(Collectors.toSet());

              return servingService.delete(servingIds);
            });
  }
}

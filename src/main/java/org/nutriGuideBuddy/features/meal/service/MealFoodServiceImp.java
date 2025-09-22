package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NOT_FOUND_BY_ID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.*;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.CustomMealFoodRepository;
import org.nutriGuideBuddy.features.meal.repository.MealFoodRepository;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.mappers.MealFoodMapper;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealFoodServiceImp implements MealFoodService {

  private final MealFoodRepository mealFoodRepository;
  private final CustomMealFoodRepository customMealFoodRepository;
  private final MealFoodServingService mealFoodServingService;
  private final MealFoodNutritionService mealFoodNutritionService;
  private final MealFoodMapper foodMapper;
  private final TransactionalOperator operator;
  private MealService mealService;

  @Autowired
  public void setMealService(@Lazy MealService mealService) {
    this.mealService = mealService;
  }

  @Override
  public Mono<MealFoodView> create(MealFoodCreateRequest dto, Long mealId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> create(dto, mealId, userId))
        .as(operator::transactional);
  }

  @Override
  public Mono<MealFoodView> create(MealFoodCreateRequest dto, Long mealId, Long userId) {
    return mealService
        .getById(mealId)
        .flatMap(
            mealView -> {
              MealFood mealFood = foodMapper.toEntity(dto);
              mealFood.setMealId(mealId);
              mealFood.setUserId(userId);

              LocalDate createdAtLdt = mealView.createdAt();
              Instant createdAt = createdAtLdt.atStartOfDay(ZoneOffset.UTC).toInstant();
              mealFood.setCreatedAt(createdAt);

              return mealFoodRepository
                  .save(mealFood)
                  .flatMap(
                      savedMealFood -> {
                        Long foodId = savedMealFood.getId();

                        Flux<ServingView> servingFlux =
                            (dto.servings() != null)
                                ? mealFoodServingService.create(dto.servings(), foodId)
                                : Flux.empty();

                        Flux<NutritionView> nutritionFlux =
                            (dto.nutrients() != null)
                                ? mealFoodNutritionService.create(dto.nutrients(), foodId)
                                : Flux.empty();

                        return Mono.zip(servingFlux.collectList(), nutritionFlux.collectList())
                            .map(
                                tuple ->
                                    foodMapper.toView(
                                        savedMealFood,
                                        Set.copyOf(tuple.getT1()),
                                        Set.copyOf(tuple.getT2())));
                      });
            })
        .as(operator::transactional);
  }

  public Flux<MealFoodView> getAll(Long mealId, MealFoodFilter filter) {
    return customMealFoodRepository
        .findAllByMealIdAndFilter(mealId, filter)
        .map(foodMapper::toView);
  }

  public Mono<MealFoodView> getById(Long foodId) {
    return customMealFoodRepository.findById(foodId).map(foodMapper::toView);
  }

  public Mono<Void> delete(Long id, Long mealId) {
    return Mono.defer(
            () -> {
              Mono<Void> deleteServings = mealFoodServingService.deleteServingsForFoodId(id);
              Mono<Void> deleteNutritions = mealFoodNutritionService.deleteNutritionsForFoodId(id);
              Mono<Void> deleteMealFood = mealFoodRepository.deleteByIdAndMealId(id, mealId);

              return Mono.when(deleteServings, deleteNutritions).then(deleteMealFood);
            })
        .as(operator::transactional);
  }

  // Do not call the repository delete method here.
  // When a meal is deleted, its foods are removed automatically via cascade.
  // Only delete associated entities (servings, nutritions) explicitly.
  public Mono<Void> deleteAllByMealIdsIn(Set<Long> mealIds) {
    if (mealIds == null || mealIds.isEmpty()) {
      return Mono.empty();
    }

    return mealFoodRepository
        .findByMealIdIn(mealIds)
        .collectList()
        .flatMap(
            mealFoods -> {
              if (mealFoods.isEmpty()) {
                return Mono.empty();
              }

              Set<Long> mealFoodIds =
                  mealFoods.stream().map(MealFood::getId).collect(Collectors.toSet());

              Mono<Void> deleteServings =
                  mealFoodServingService.deleteServingsForFoodIdIn(mealFoodIds);
              Mono<Void> deleteNutritions =
                  mealFoodNutritionService.deleteNutritionsForFoodIdIn(mealFoodIds);

              return Mono.when(deleteServings, deleteNutritions).then();
            })
        .as(operator::transactional);
  }

  public Mono<MealFoodView> update(MealFoodUpdateRequest dto, Long foodId, Long mealId) {
    return findByIdAndMealIdOrThrow(foodId, mealId)
        .flatMap(
            existingMealFood -> {
              foodMapper.update(dto, existingMealFood);

              return mealFoodRepository
                  .save(existingMealFood)
                  .flatMap(
                      savedMealFood -> {
                        Flux<ServingView> servingFlux =
                            (dto.servings() != null)
                                ? mealFoodServingService.update(dto.servings(), foodId)
                                : Flux.empty();

                        Flux<NutritionView> nutritionFlux =
                            (dto.nutrients() != null)
                                ? mealFoodNutritionService.update(dto.nutrients(), foodId)
                                : Flux.empty();

                        return Mono.zip(servingFlux.collectList(), nutritionFlux.collectList())
                            .map(
                                tuple ->
                                    foodMapper.toView(
                                        savedMealFood,
                                        Set.copyOf(tuple.getT1()),
                                        Set.copyOf(tuple.getT2())));
                      });
            })
        .as(operator::transactional);
  }

  public Mono<Boolean> existsByIdAndMealId(Long id, Long mealId) {
    return mealFoodRepository.existsByIdAndMealId(id, mealId);
  }

  public Mono<MealFood> findByIdAndMealIdOrThrow(Long id, Long mealId) {
    return mealFoodRepository
        .findByIdAndMealId(id, mealId)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(NOT_FOUND_BY_ID, "MealFood", id))));
  }

  public Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter) {
    return customMealFoodRepository.countByMealIdAndFilter(mealId, filter);
  }

  @Override
  public Mono<Double> sumConsumedCaloriesByUserIdAndDate(Long userId, LocalDate date) {
    return mealFoodRepository.sumCaloriesByUserIdOnDate(userId, date);
  }
}

package org.nutriGuideBuddy.features.meal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.*;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.MealFoodCustomRepository;
import org.nutriGuideBuddy.features.meal.repository.MealFoodRepository;
import org.nutriGuideBuddy.features.shared.dto.*;
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

  private final MealFoodRepository repository;
  private final MealFoodCustomRepository customRepository;
  private final MealFoodServingService servingService;
  private final MealFoodNutritionService nutritionService;
  private final MealFoodMapper mapper;
  private final TransactionalOperator operator;
  private MealService mealService;

  @Autowired
  public void setMealService(@Lazy MealService mealService) {
    this.mealService = mealService;
  }

  @Override
  public Mono<MealFoodView> create(FoodCreateRequest dto, Long mealId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> create(dto, mealId, userId))
        .as(operator::transactional);
  }

  @Override
  public Mono<MealFoodView> create(FoodCreateRequest dto, Long mealId, Long userId) {
    return mealService
        .getById(mealId)
        .flatMap(
            mealView -> {
              var entity = mapper.toEntity(dto);
              entity.setMealId(mealId);
              entity.setUserId(userId);

              var createdAtLdt = mealView.createdAt();
              Instant createdAt = createdAtLdt.atStartOfDay(ZoneOffset.UTC).toInstant();
              entity.setCreatedAt(createdAt);

              return repository
                  .save(entity)
                  .flatMap(
                      savedMealFood -> {
                        Long foodId = savedMealFood.getId();

                        Flux<ServingView> servingFlux =
                            (dto.servings() != null)
                                ? servingService.create(dto.servings(), foodId)
                                : Flux.empty();

                        Flux<NutritionView> nutritionFlux =
                            (dto.nutrients() != null)
                                ? nutritionService.create(dto.nutrients(), foodId)
                                : Flux.empty();

                        return Mono.zip(servingFlux.collectList(), nutritionFlux.collectList())
                            .map(
                                tuple ->
                                    mapper.toView(
                                        savedMealFood,
                                        Set.copyOf(tuple.getT1()),
                                        Set.copyOf(tuple.getT2())));
                      });
            })
        .as(operator::transactional);
  }

  public Flux<MealFoodView> getAll(Long mealId, MealFoodFilter filter) {
    return customRepository.findAllByMealIdAndFilter(mealId, filter).map(mapper::toView);
  }

  public Mono<MealFoodView> getById(Long id) {
    return customRepository
        .findById(id)
        .switchIfEmpty(Mono.error(NotFoundException.byId(MealFood.class.getSimpleName(), id)))
        .map(mapper::toView);
  }

  public Mono<Void> delete(Long id, Long mealId) {
    return repository.deleteByIdAndMealId(id, mealId).as(operator::transactional);
  }

  public Mono<MealFoodView> update(FoodUpdateRequest dto, Long id, Long mealId) {
    return findByIdAndMealIdOrThrow(id, mealId)
        .flatMap(
            existingMealFood -> {
              mapper.update(dto, existingMealFood);

              return repository
                  .save(existingMealFood)
                  .flatMap(
                      savedMealFood -> {
                        var servingFlux = servingService.update(dto.servings(), id);
                        var nutritionFlux =
                            nutritionService.update(dto.nutrients(), id);
                        return Mono.zip(servingFlux.collectList(), nutritionFlux.collectList())
                            .map(
                                tuple ->
                                    mapper.toView(
                                        savedMealFood,
                                        Set.copyOf(tuple.getT1()),
                                        Set.copyOf(tuple.getT2())));
                      });
            })
        .as(operator::transactional);
  }

  public Mono<Boolean> existsByIdAndMealId(Long id, Long mealId) {
    return repository.existsByIdAndMealId(id, mealId);
  }

  public Mono<MealFood> findByIdAndMealIdOrThrow(Long id, Long mealId) {
    return repository
        .findByIdAndMealId(id, mealId)
        .switchIfEmpty(Mono.error(NotFoundException.byId(MealFood.class.getSimpleName(), id)));
  }

  public Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter) {
    return customRepository.countByMealIdAndFilter(mealId, filter);
  }
}

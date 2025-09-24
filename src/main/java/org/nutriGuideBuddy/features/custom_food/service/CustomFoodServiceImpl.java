package org.nutriGuideBuddy.features.custom_food.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NOT_FOUND_BY_ID;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodCustomRepository;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodRepository;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.infrastructure.mappers.CustomFoodMapper;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomFoodServiceImpl {

  private final CustomFoodRepository repository;
  private final CustomFoodCustomRepository customMealFoodRepository;
  private final CustomFoodNutritionServiceImpl nutritionService;
  private final CustomFoodServingServiceImpl servingService;
  private final CustomFoodMapper mapper;
  private final TransactionalOperator operator;

  public Mono<CustomFoodView> create(FoodCreateRequest dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                repository
                    .existsByNameAndUserId(dto.name(), userId)
                    .flatMap(
                        exists -> {
                          if (Boolean.TRUE.equals(exists)) {
                            return Mono.error(
                                new ValidationException(Map.of("name", "already exists")));
                          }
                          var entity = mapper.toEntity(dto);
                          entity.setUserId(userId);
                          return repository
                              .save(entity)
                              .flatMap(
                                  saved -> {
                                    Long foodId = saved.getId();

                                    Flux<ServingView> servingFlux =
                                        dto.servings() != null
                                            ? servingService.create(dto.servings(), foodId)
                                            : Flux.empty();

                                    Flux<NutritionView> nutritionFlux =
                                        dto.nutrients() != null
                                            ? nutritionService.create(dto.nutrients(), foodId)
                                            : Flux.empty();

                                    return Mono.zip(
                                            servingFlux.collectList(), nutritionFlux.collectList())
                                        .map(
                                            tuple ->
                                                mapper.toView(
                                                    saved,
                                                    Set.copyOf(tuple.getT1()),
                                                    Set.copyOf(tuple.getT2())));
                                  });
                        }))
        .as(operator::transactional);
  }

  public Mono<Long> countByFilter(CustomFoodFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> customMealFoodRepository.countByUserIdAndFilter(userId, filter));
  }

  public Flux<CustomFoodView> getAll(CustomFoodFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMapMany(
            userId ->
                customMealFoodRepository
                    .findAllByUserIdAndFilter(userId, filter)
                    .map(mapper::toView));
  }

  public Mono<CustomFoodView> getById(Long id) {
    return customMealFoodRepository.findById(id).map(mapper::toView);
  }

  public Mono<CustomFoodView> update(FoodUpdateRequest dto, Long id) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                repository
                    .findByIdAndUserId(id, userId)
                    .switchIfEmpty(
                        Mono.error(
                            new NotFoundException(
                                String.format(NOT_FOUND_BY_ID, "CustomFood", id))))
                    .flatMap(
                        existing -> {
                          mapper.update(dto, existing);

                          return repository
                              .save(existing)
                              .flatMap(
                                  saved -> {
                                    Flux<ServingView> servingFlux =
                                        dto.servings() != null
                                            ? servingService.update(dto.servings(), id)
                                            : Flux.empty();

                                    Flux<NutritionView> nutritionFlux =
                                        dto.nutrients() != null
                                            ? nutritionService.update(dto.nutrients(), id)
                                            : Flux.empty();

                                    return Mono.zip(
                                            servingFlux.collectList(), nutritionFlux.collectList())
                                        .map(
                                            tuple ->
                                                mapper.toView(
                                                    saved,
                                                    Set.copyOf(tuple.getT1()),
                                                    Set.copyOf(tuple.getT2())));
                                  });
                        }))
        .as(operator::transactional);
  }

  public Mono<Void> delete(Long id) {
    return Mono.defer(
            () -> {
              Mono<Void> deleteServings = servingService.deleteServingsForFoodId(id);
              Mono<Void> deleteNutritions = nutritionService.deleteNutritionsForFoodId(id);
              Mono<Void> deleteCustomFood = repository.deleteById(id);

              return Mono.when(deleteServings, deleteNutritions).then(deleteCustomFood);
            })
        .as(operator::transactional);
  }

  // Do not call the repository delete method here.
  // When a user is deleted, its foods are removed automatically via cascade.
  // Only delete associated entities (servings, nutritions) explicitly.
  public Mono<Void> deleteAllByUserId(Long userId) {
    return repository
        .findAllByUserId(userId)
        .collectList()
        .flatMap(
            customFoods -> {
              if (customFoods.isEmpty()) {
                return Mono.empty();
              }

              Set<Long> mealFoodIds =
                  customFoods.stream().map(CustomFood::getId).collect(Collectors.toSet());

              Mono<Void> deleteServings = servingService.deleteServingsForFoodIdIn(mealFoodIds);
              Mono<Void> deleteNutritions =
                  nutritionService.deleteNutritionsForFoodIdIn(mealFoodIds);

              return Mono.when(deleteServings, deleteNutritions).then();
            })
        .as(operator::transactional);
  }

  public Mono<Boolean> existsByIdAndUserId(Long id, Long userId) {
    return repository.existsByIdAndUserId(id, userId);
  }
}

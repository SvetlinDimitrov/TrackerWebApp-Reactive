package org.nutriGuideBuddy.features.custom_food.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodCustomRepository;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodRepository;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
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
public class CustomFoodServiceImpl implements CustomFoodService {

  private final CustomFoodRepository repository;
  private final CustomFoodCustomRepository customRepository;
  private final CustomFoodNutritionService nutritionService;
  private final CustomFoodServingService servingService;
  private final CustomFoodMapper mapper;
  private final TransactionalOperator operator;

  public Mono<CustomFoodView> create(FoodCreateRequest dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> create(dto, userId))
        .as(operator::transactional);
  }

  public Mono<CustomFoodView> create(FoodCreateRequest dto, Long userId) {
    return repository
        .existsByNameAndUserId(dto.name(), userId)
        .flatMap(
            exists -> {
              if (Boolean.TRUE.equals(exists)) {
                return Mono.error(
                    ValidationException.duplicate(CustomFood.class.getSimpleName(), "name"));
              }
              var entity = mapper.toEntity(dto);
              entity.setUserId(userId);
              return repository
                  .save(entity)
                  .flatMap(
                      saved -> {
                        var foodId = saved.getId();
                        var servingFlux = servingService.create(dto.servings(), foodId);
                        var nutritionFlux = nutritionService.create(dto.nutrients(), foodId);
                        return Mono.zip(servingFlux.collectList(), nutritionFlux.collectList())
                            .map(
                                tuple ->
                                    mapper.toView(
                                        saved,
                                        Set.copyOf(tuple.getT1()),
                                        Set.copyOf(tuple.getT2())));
                      });
            })
        .as(operator::transactional);
  }

  public Mono<Long> countByFilter(CustomFoodFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> customRepository.countByUserIdAndFilter(userId, filter));
  }

  public Flux<CustomFoodView> getAll(CustomFoodFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMapMany(
            userId ->
                customRepository.findAllByUserIdAndFilter(userId, filter).map(mapper::toView));
  }

  public Mono<CustomFoodView> getById(Long id) {
    return customRepository.findById(id).map(mapper::toView);
  }

  public Mono<CustomFoodView> update(FoodUpdateRequest dto, Long id) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                repository
                    .findByIdAndUserId(id, userId)
                    .switchIfEmpty(
                        Mono.error(NotFoundException.byId(CustomFood.class.getSimpleName(), id)))
                    .flatMap(
                        existing -> {
                          Mono<Boolean> nameIsFree;

                          if (dto.name() != null
                              && !dto.name().equalsIgnoreCase(existing.getName())) {
                            nameIsFree =
                                repository
                                    .existsByNameAndUserIdAndIdNot(dto.name(), userId, id)
                                    .map(exists -> !Boolean.TRUE.equals(exists));
                          } else {
                            nameIsFree = Mono.just(true);
                          }

                          return nameIsFree.flatMap(
                              isFree -> {
                                if (!isFree) {
                                  return Mono.error(
                                      ValidationException.duplicate(
                                          CustomFood.class.getSimpleName(), "name"));
                                }

                                mapper.update(dto, existing);

                                return repository
                                    .save(existing)
                                    .flatMap(
                                        saved -> {
                                          var servingFlux =
                                              servingService.update(dto.servings(), id);
                                          var nutritionFlux =
                                              nutritionService.update(dto.nutrients(), id);

                                          return Mono.zip(
                                                  servingFlux.collectList(),
                                                  nutritionFlux.collectList())
                                              .map(
                                                  tuple ->
                                                      mapper.toView(
                                                          saved,
                                                          Set.copyOf(tuple.getT1()),
                                                          Set.copyOf(tuple.getT2())));
                                        });
                              });
                        }))
        .as(operator::transactional);
  }

  public Mono<Void> delete(Long id) {
    return repository.deleteById(id).as(operator::transactional);
  }

  public Mono<Boolean> existsByIdAndUserId(Long id, Long userId) {
    return repository.existsByIdAndUserId(id, userId);
  }

  public Mono<Long> countByUserId(Long userId) {
    return repository.countByUserId(userId);
  }
}

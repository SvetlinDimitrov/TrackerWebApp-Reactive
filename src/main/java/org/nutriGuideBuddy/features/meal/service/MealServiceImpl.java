package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NOT_FOUND_BY_ID;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.CustomMealRepository;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.infrastructure.mappers.MealMapper;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealServiceImpl {

  private final CustomMealRepository customRepository;
  private final MealRepository repository;
  private final MealMapper mealMapper;

  public Flux<MealView> getAll() {
    return customRepository.findAllMealsWithShortFoodDetails().map(mealMapper::toView);
  }

  public Mono<MealView> getById(Long id) {
    return customRepository.findById(id).map(mealMapper::toView);
  }

  public Mono<MealView> create(MealCreateRequest dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(
            userId ->
                repository
                    .existsByNameAndUserId(dto.name(), userId)
                    .flatMap(
                        exists -> {
                          if (exists) {
                            Map<String, String> errors = Map.of("name", "name already exists.");
                            return Mono.error(new ValidationException(errors));
                          }
                          Meal meal = mealMapper.toEntity(dto);
                          meal.setUserId(userId);
                          return repository.save(meal);
                        }))
        .flatMap(meal -> getById(meal.getId()));
  }

  public Mono<MealView> updateById(MealUpdateRequest dto, Long id) {
    return findByIdOrThrow(id)
        .flatMap(
            entity ->
                repository
                    .existsByNameAndUserIdAndIdNot(dto.name(), entity.getUserId(), id)
                    .flatMap(
                        exists -> {
                          if (exists) {
                            Map<String, String> errors = Map.of("name", "name already exists.");
                            return Mono.error(new ValidationException(errors));
                          }
                          mealMapper.update(dto, entity);
                          return repository.save(entity);
                        }))
        .flatMap(meal -> getById(meal.getId()));
  }

  public Mono<Void> deleteById(Long id) {
    return repository.deleteById(id);
  }

  public Mono<Boolean> existsByIdAndUserId(Long id, Long userId) {
    return repository.existsByIdAndUserId(id, userId);
  }

  private Mono<Meal> findByIdOrThrow(Long mealId) {
    return repository
        .findById(mealId)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(NOT_FOUND_BY_ID, "Meal", mealId))));
  }
}

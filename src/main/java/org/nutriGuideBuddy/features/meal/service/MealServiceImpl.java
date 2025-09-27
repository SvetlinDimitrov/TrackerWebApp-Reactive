package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NOT_FOUND_BY_ID;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.MealCustomRepository;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.infrastructure.mappers.MealMapper;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

  private final MealCustomRepository customRepository;
  private final MealRepository repository;
  private final MealMapper mapper;
  private final TransactionalOperator operator;

  public Flux<MealView> getAll(MealFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMapMany(
            userId -> customRepository.findAllWithFoodDetailsByFilterAndUserId(filter, userId))
        .map(mapper::toView);
  }

  public Mono<Long> count(MealFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> customRepository.countByFilterAndUserId(filter, userId));
  }

  public Mono<MealView> getById(Long id) {
    return customRepository.findById(id).map(mapper::toView);
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
                          Meal meal = mapper.toEntity(dto);
                          meal.setUserId(userId);
                          return repository.save(meal);
                        }))
        .flatMap(meal -> getById(meal.getId()))
        .as(operator::transactional);
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
                          mapper.update(dto, entity);
                          return repository.save(entity);
                        }))
        .flatMap(meal -> getById(meal.getId()))
        .as(operator::transactional);
  }

  public Mono<Void> deleteById(Long id) {
    return repository.deleteById(id).as(operator::transactional);
  }

  public Mono<Boolean> existsByIdAndUserId(Long id, Long userId) {
    return repository.existsByIdAndUserId(id, userId);
  }

  public Flux<MealConsumedView> getAllConsumedByDateAndUserId(Long userId, LocalDate date) {
    return customRepository
        .findMealsConsumtionWithFoodsByUserIdAndDate(userId, date)
        .map(mapper::toConsumedView);
  }

  public Mono<Map<LocalDate, Set<MealConsumedView>>> getCaloriesInRange(
      LocalDate startDate, LocalDate endDate, Long userId) {

    return customRepository
        .findUserCaloriesDailyAmounts(userId, startDate, endDate)
        .map(
            dailyMap -> {
              Map<LocalDate, Set<MealConsumedView>> result = new LinkedHashMap<>();
              dailyMap.forEach(
                  (day, meals) -> {
                    Set<MealConsumedView> views =
                        meals.stream().map(mapper::toConsumedView).collect(Collectors.toSet());
                    result.put(day, views);
                  });
              return result;
            });
  }

  private Mono<Meal> findByIdOrThrow(Long mealId) {
    return repository
        .findById(mealId)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(NOT_FOUND_BY_ID, "Meal", mealId))));
  }
}

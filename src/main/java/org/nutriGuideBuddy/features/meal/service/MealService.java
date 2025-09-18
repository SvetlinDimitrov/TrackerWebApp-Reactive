package org.nutriGuideBuddy.features.meal.service;

import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MealService {

  Flux<MealView> getAll(MealFilter filter);

  Mono<Long> count(MealFilter filter);

  Mono<MealView> getById(Long id);

  Mono<MealView> create(MealCreateRequest dto);

  Mono<MealView> updateById(MealUpdateRequest dto, Long id);

  Mono<Void> deleteById(Long id);

  Mono<Void> deleteAllByUserId(Long userId);

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  Flux<MealConsumedView> getAllConsumedByDateAndUserId(Long userId, LocalDate date);
}

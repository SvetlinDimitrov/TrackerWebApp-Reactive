package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedDetailedView;
import org.nutriGuideBuddy.features.meal.dto.MealFoodNutritionConsumedView;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.MealFoodNutritionCustomRepository;
import org.nutriGuideBuddy.features.meal.repository.MealFoodNutritionRepository;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.MealFoodNutritionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MealFoodNutritionServiceImpl implements MealFoodNutritionService {

  private final MealFoodNutritionRepository repository;
  private final MealFoodNutritionCustomRepository customRepository;
  private final MealFoodNutritionMapper mapper;
  private final TransactionalOperator operator;

  @Override
  public Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    return Flux.fromIterable(requests)
        .map(
            req -> {
              var e = mapper.toEntity(req);
              e.setFoodId(foodId);
              return e;
            })
        .collectList()
        .flatMapMany(repository::saveAll)
        .map(mapper::toView)
        .as(operator::transactional);
  }

  @Override
  public Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return repository.findAllByFoodId(foodId).map(mapper::toView);
    }

    return repository
        .findAllByFoodId(foodId)
        .collectList()
        .flatMapMany(
            existing -> {
              Set<Long> existingIds =
                  existing.stream().map(BaseEntity::getId).collect(Collectors.toSet());

              Set<Long> requestedIds =
                  requests.stream().map(NutritionUpdateRequest::id).collect(Collectors.toSet());
              requestedIds.removeAll(existingIds);

              if (!requestedIds.isEmpty()) {
                return Flux.error(
                    new BadRequestException(
                        String.format(
                            NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID,
                            requestedIds,
                            MealFood.class.getSimpleName(),
                            foodId)));
              }
              Map<Long, NutritionUpdateRequest> byId =
                  requests.stream()
                      .filter(r -> r.id() != null)
                      .collect(Collectors.toMap(NutritionUpdateRequest::id, r -> r));

              existing.forEach(
                  e -> {
                    var r = byId.get(e.getId());
                    if (r != null) {
                      mapper.update(r, e);
                    }
                  });

              return repository.saveAll(existing).map(mapper::toView);
            })
        .as(operator::transactional);
  }

  @Override
  public Mono<Map<String, MealFoodNutritionConsumedDetailedView>> findUserDailyNutrition(
      Long userId, LocalDate date) {
    return customRepository
        .findUserDailyNutrition(userId, date)
        .map(
            resultMap ->
                resultMap.entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            Map.Entry::getKey, e -> mapper.toConsumedDetailedView(e.getValue()))));
  }

  @Override
  public Mono<Map<LocalDate, Set<MealFoodNutritionConsumedView>>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate) {
    return customRepository
        .findUserNutritionDailyAmounts(userId, nutritionName, startDate, endDate)
        .map(
            resultMap ->
                resultMap.entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            Map.Entry::getKey,
                            e ->
                                e.getValue().stream()
                                    .map(mapper::toConsumedView)
                                    .collect(Collectors.toSet()))));
  }
}

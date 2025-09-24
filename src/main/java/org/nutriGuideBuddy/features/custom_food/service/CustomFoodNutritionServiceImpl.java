package org.nutriGuideBuddy.features.custom_food.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodNutrition;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodNutritionRepository;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.service.NutritionServiceImpl;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.NutritionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomFoodNutritionServiceImpl {

  private final CustomFoodNutritionRepository repository;
  private final NutritionServiceImpl nutritionService;
  private final NutritionMapper mapper;
  private final TransactionalOperator operator;

  public Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long customFoodId) {
    return nutritionService
        .create(requests)
        .collectList()
        .flatMapMany(
            nutritions -> {
              var joins =
                  nutritions.stream()
                      .map(n -> new CustomFoodNutrition(customFoodId, n.getId()))
                      .toList();

              return repository
                  .saveAll(joins)
                  .thenMany(Flux.fromIterable(nutritions).map(mapper::toView));
            })
        .as(operator::transactional);
  }

  public Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long customFoodId) {
    Set<Long> requestedIds =
        requests.stream().map(NutritionUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findByCustomFoodId(customFoodId)
        .map(CustomFoodNutrition::getNutritionId)
        .collectList()
        .flatMapMany(
            existingIds -> {
              HashSet<Long> existing = new HashSet<>(existingIds);

              Set<Long> missing =
                  requestedIds.stream()
                      .filter(id -> !existing.contains(id))
                      .collect(Collectors.toSet());

              if (!missing.isEmpty()) {
                return Flux.error(
                    new BadRequestException(
                        String.format(
                            NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID,
                            missing,
                            "custom food",
                            customFoodId)));
              }

              return nutritionService.updateAndFetch(requests, existing).map(mapper::toView);
            })
        .as(operator::transactional);
  }

  public Mono<Void> deleteNutritionsForFoodId(Long customFoodId) {
    return repository
        .findByCustomFoodId(customFoodId)
        .collectList()
        .flatMap(
            joins -> {
              if (joins.isEmpty()) return Mono.empty();
              Set<Long> nutritionIds =
                  joins.stream()
                      .map(CustomFoodNutrition::getNutritionId)
                      .collect(Collectors.toSet());
              return nutritionService.delete(nutritionIds);
            })
        .as(operator::transactional);
  }

  public Mono<Void> deleteNutritionsForFoodIdIn(Set<Long> customFoodIds) {
    if (customFoodIds == null || customFoodIds.isEmpty()) return Mono.empty();

    return repository
        .findByCustomFoodIdIn(customFoodIds)
        .collectList()
        .flatMap(
            joins -> {
              if (joins.isEmpty()) return Mono.empty();
              Set<Long> nutritionIds =
                  joins.stream()
                      .map(CustomFoodNutrition::getNutritionId)
                      .collect(Collectors.toSet());
              return nutritionService.delete(nutritionIds);
            })
        .as(operator::transactional);
  }
}

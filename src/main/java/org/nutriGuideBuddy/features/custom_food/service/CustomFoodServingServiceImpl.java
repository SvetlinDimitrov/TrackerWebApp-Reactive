package org.nutriGuideBuddy.features.custom_food.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.SERVINGS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodServing;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodServingRepository;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.features.shared.service.ServingServiceImpl;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.ServingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomFoodServingServiceImpl {

  private final CustomFoodServingRepository repository;
  private final ServingServiceImpl servingService;
  private final ServingMapper mapper;
  private final TransactionalOperator operator;

  public Flux<ServingView> create(Set<ServingCreateRequest> requests, Long customFoodId) {
    return servingService
        .create(requests)
        .collectList()
        .flatMapMany(
            servings -> {
              var joins =
                  servings.stream()
                      .map(s -> new CustomFoodServing(customFoodId, s.getId()))
                      .toList();

              return repository
                  .saveAll(joins)
                  .thenMany(Flux.fromIterable(servings).map(mapper::toView));
            })
        .as(operator::transactional);
  }

  public Flux<ServingView> update(Set<ServingUpdateRequest> requests, Long customFoodId) {
    Set<Long> ids = requests.stream().map(ServingUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findByCustomFoodId(customFoodId)
        .map(CustomFoodServing::getServingId)
        .collectList()
        .flatMapMany(
            existingIds -> {
              HashSet<Long> existing = new HashSet<>(existingIds);
              Set<Long> missing =
                  ids.stream().filter(id -> !existing.contains(id)).collect(Collectors.toSet());

              if (!missing.isEmpty()) {
                return Flux.error(
                    new BadRequestException(
                        String.format(SERVINGS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID,
                            missing,
                            "custom food",
                            customFoodId)));
              }

              return servingService.updateAndFetchAll(requests, existing).map(mapper::toView);
            })
        .as(operator::transactional);
  }

  public Mono<Void> deleteServingsForFoodId(Long customFoodId) {
    return repository
        .findByCustomFoodId(customFoodId)
        .collectList()
        .flatMap(
            joins -> {
              if (joins.isEmpty()) return Mono.empty();
              Set<Long> servingIds =
                  joins.stream().map(CustomFoodServing::getServingId).collect(Collectors.toSet());
              return servingService.delete(servingIds);
            })
        .as(operator::transactional);
  }

  public Mono<Void> deleteServingsForFoodIdIn(Set<Long> customFoodIds) {
    if (customFoodIds == null || customFoodIds.isEmpty()) return Mono.empty();

    return repository
        .findByCustomFoodIdIn(customFoodIds)
        .collectList()
        .flatMap(
            joins -> {
              if (joins.isEmpty()) return Mono.empty();
              Set<Long> servingIds =
                  joins.stream().map(CustomFoodServing::getServingId).collect(Collectors.toSet());
              return servingService.delete(servingIds);
            })
        .as(operator::transactional);
  }
}

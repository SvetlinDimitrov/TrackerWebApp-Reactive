package org.nutriGuideBuddy.features.meal.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.EXACTLY_ONE_MAIN_SERVING_AFTER_UPDATE;
import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.SERVINGS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.entity.MealFoodServing;
import org.nutriGuideBuddy.features.meal.repository.MealFoodServingRepository;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.MealFoodServingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MealFoodServingServiceImpl implements MealFoodServingService {

  private final MealFoodServingRepository repository;
  private final MealFoodServingMapper mapper;
  private final TransactionalOperator operator;

  public Flux<ServingView> create(Set<ServingCreateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    return Flux.fromIterable(requests)
        .map(
            req -> {
              var entity = mapper.toEntity(req);
              entity.setFoodId(foodId);
              return entity;
            })
        .collectList()
        .flatMapMany(repository::saveAll)
        .map(mapper::toView)
        .as(operator::transactional);
  }

  public Flux<ServingView> update(Set<ServingUpdateRequest> requests, Long foodId) {
    if (requests == null || requests.isEmpty()) {
      return repository.findAllByFoodId(foodId).map(mapper::toView);
    }

    return repository
        .findAllByFoodId(foodId)
        .collectList()
        .flatMapMany(
            existing -> {
              var existingIds =
                  existing.stream().map(BaseEntity::getId).collect(Collectors.toSet());

              var requestedIds =
                  requests.stream().map(ServingUpdateRequest::id).collect(Collectors.toSet());

              requestedIds.removeAll(existingIds);
              if (!requestedIds.isEmpty()) {
                return Flux.error(
                    BadRequestException.message(
                        String.format(
                            SERVINGS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID,
                            requestedIds,
                            MealFood.class.getSimpleName(),
                            foodId)));
              }

              Map<Long, ServingUpdateRequest> byId =
                  requests.stream()
                      .filter(r -> r.id() != null)
                      .collect(Collectors.toMap(ServingUpdateRequest::id, r -> r));

              existing.forEach(
                  e -> {
                    var request = byId.get(e.getId());
                    if (request != null) {
                      mapper.update(request, e);
                    }
                  });

              var mainCount =
                  existing.stream().filter(s -> Boolean.TRUE.equals(s.getMain())).count();

              if (mainCount != 1) {
                return Flux.error(
                    BadRequestException.message(
                        String.format(EXACTLY_ONE_MAIN_SERVING_AFTER_UPDATE, mainCount)));
              }

              return repository.saveAll(existing).map(mapper::toView);
            })
        .as(operator::transactional);
  }
}

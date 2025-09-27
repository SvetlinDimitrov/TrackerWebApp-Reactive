package org.nutriGuideBuddy.features.custom_food.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodNutrition;
import org.nutriGuideBuddy.features.custom_food.repository.CustomFoodNutritionRepository;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionView;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.CustomFoodNutritionMapper; // <- use a mapper for
// CustomFoodNutrition
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomFoodNutritionServiceImpl implements CustomFoodNutritionService {

  private final CustomFoodNutritionRepository repository;
  private final CustomFoodNutritionMapper mapper;
  private final TransactionalOperator operator;

  public Flux<NutritionView> create(Set<NutritionCreateRequest> requests, Long customFoodId) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    return Flux.fromIterable(requests)
        .map(
            req -> {
              CustomFoodNutrition e = mapper.toEntity(req);
              e.setFoodId(customFoodId);
              return e;
            })
        .collectList()
        .flatMapMany(repository::saveAll)
        .map(mapper::toView)
        .as(operator::transactional);
  }

  public Flux<NutritionView> update(Set<NutritionUpdateRequest> requests, Long customFoodId) {
    if (requests == null || requests.isEmpty()) {
      return repository.findAllByFoodId(customFoodId).map(mapper::toView);
    }

    return repository
        .findAllByFoodId(customFoodId)
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
                    BadRequestException.message(
                        String.format(
                            NUTRITIONS_WITH_IDS_DO_NOT_BELONG_TO_WITH_ID,
                            requestedIds,
                            CustomFood.class.getSimpleName(),
                            customFoodId)));
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
}

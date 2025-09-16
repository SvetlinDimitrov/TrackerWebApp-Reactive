package org.nutriGuideBuddy.features.shared.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.NutritionUpdateRequest;
import org.nutriGuideBuddy.features.shared.entity.Nutrition;
import org.nutriGuideBuddy.features.shared.repository.NutritionRepository;
import org.nutriGuideBuddy.infrastructure.mappers.NutritionMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl {

  private final NutritionRepository repository;
  private final NutritionMapper mapper;

  public Flux<Nutrition> create(Set<NutritionCreateRequest> requests) {
    return Flux.fromIterable(requests)
        .map(mapper::toEntity)
        .collectList()
        .flatMapMany(repository::saveAll);
  }

  public Flux<Nutrition> updateAndFetch(Set<NutritionUpdateRequest> requests, Set<Long> fetchIds) {
    Set<Long> requestIds =
        requests.stream().map(NutritionUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findAllById(requestIds)
        .collectList()
        .flatMapMany(
            existingEntities -> {
              var requestMap =
                  requests.stream()
                      .collect(Collectors.toMap(NutritionUpdateRequest::id, req -> req));

              existingEntities.forEach(
                  entity -> {
                    NutritionUpdateRequest req = requestMap.get(entity.getId());
                    if (req != null) {
                      mapper.update(req, entity);
                    }
                  });

              // Save updated entities first, then fetch all requested IDs
              return repository
                  .saveAll(existingEntities)
                  .thenMany(repository.findAllById(fetchIds));
            });
  }

  public Mono<Void> delete(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.empty();
    }
    return repository.deleteAllById(ids);
  }
}

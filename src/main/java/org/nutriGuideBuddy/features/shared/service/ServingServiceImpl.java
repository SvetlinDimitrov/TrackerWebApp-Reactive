package org.nutriGuideBuddy.features.shared.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.entity.Serving;
import org.nutriGuideBuddy.features.shared.repository.ServingRepository;
import org.nutriGuideBuddy.infrastructure.mappers.ServingMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ServingServiceImpl {

  private final ServingRepository repository;
  private final ServingMapper mapper;

  public Flux<Serving> create(Set<ServingCreateRequest> requests) {
    return Flux.fromIterable(requests)
        .map(mapper::toEntity)
        .collectList()
        .flatMapMany(repository::saveAll);
  }

  public Flux<Serving> update(Set<ServingUpdateRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return Flux.empty();
    }

    Set<Long> ids = requests.stream().map(ServingUpdateRequest::id).collect(Collectors.toSet());

    return repository
        .findAllById(ids)
        .collectList()
        .flatMapMany(
            existingEntities -> {
              var requestMap =
                  requests.stream().collect(Collectors.toMap(ServingUpdateRequest::id, req -> req));

              existingEntities.forEach(
                  entity -> {
                    ServingUpdateRequest req = requestMap.get(entity.getId());
                    if (req != null) {
                      mapper.update(req, entity);
                    }
                  });

              return repository.saveAll(existingEntities);
            });
  }

  public Mono<Void> delete(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.empty();
    }
    return repository.deleteAllById(ids);
  }
}

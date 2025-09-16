package org.nutriGuideBuddy.features.shared.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.EXACTLY_ONE_MAIN_SERVING_AFTER_UPDATE;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.entity.Serving;
import org.nutriGuideBuddy.features.shared.repository.ServingRepository;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
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

  public Flux<Serving> updateAndFetchAll(Set<ServingUpdateRequest> requests, Set<Long> fetchIds) {
    return repository
        .findAllById(fetchIds)
        .collectList()
        .flatMapMany(
            existingEntities -> {
              if (requests != null && !requests.isEmpty()) {
                var requestMap =
                    requests.stream()
                        .collect(Collectors.toMap(ServingUpdateRequest::id, req -> req));

                existingEntities.forEach(
                    entity -> {
                      ServingUpdateRequest req = requestMap.get(entity.getId());
                      if (req != null) {
                        mapper.update(req, entity);
                      }
                    });
              }

              long mainCount =
                  existingEntities.stream().filter(s -> Boolean.TRUE.equals(s.getMain())).count();

              if (mainCount != 1) {
                return Flux.error(
                    new BadRequestException(
                        String.format(EXACTLY_ONE_MAIN_SERVING_AFTER_UPDATE, mainCount)));
              }

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

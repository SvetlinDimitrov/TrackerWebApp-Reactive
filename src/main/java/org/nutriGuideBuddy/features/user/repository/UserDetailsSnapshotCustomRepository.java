package org.nutriGuideBuddy.features.user.repository;

import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDetailsSnapshotCustomRepository {

  Flux<UserDetailsSnapshotProjection> findAllByFilter(
      Long userId, UserDetailsSnapshotFilter filter);

  Mono<Long> countByFilter(Long userId, UserDetailsSnapshotFilter filter);
}

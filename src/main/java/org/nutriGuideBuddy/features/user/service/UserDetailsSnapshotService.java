package org.nutriGuideBuddy.features.user.service;

import java.time.Instant;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.entity.UserDetailsSnapshot;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDetailsSnapshotService {
  Mono<Void> create(UserDetails userDetails);

  Flux<UserDetailsSnapshotProjection> findAllByFilter(UserDetailsSnapshotFilter filter);

  Mono<Long> countByFilter(UserDetailsSnapshotFilter filter);

  Mono<UserDetailsSnapshotView> get(Long id);

  Mono<UserDetailsSnapshot> findByIdOrThrow(Long id);

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  Mono<UserDetailsSnapshotView> findLatestByUserIdAndDate(Long userId, Instant dateEnd);
}

package org.nutriGuideBuddy.features.user.repository;

import java.time.Instant;
import org.nutriGuideBuddy.features.user.entity.UserDetailsSnapshot;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDetailsSnapshotRepository extends R2dbcRepository<UserDetailsSnapshot, Long> {

  Mono<Boolean> existsByIdAndUserId(Long id, Long userId);

  @Query(
      """
      SELECT * FROM user_details_snapshots uds
      WHERE uds.user_id = :userId
        AND uds.created_at <= :dateEnd
      ORDER BY uds.created_at DESC
      LIMIT 1
      """)
  Mono<UserDetailsSnapshot> findLatestByUserIdAndDate(Long userId, Instant dateEnd);
}

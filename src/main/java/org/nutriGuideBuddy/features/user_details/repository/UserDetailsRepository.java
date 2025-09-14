package org.nutriGuideBuddy.features.user_details.repository;

import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDetailsRepository extends R2dbcRepository<UserDetails, Long> {

  Mono<UserDetails> findByUserId(Long userId);

  Mono<Boolean> existsByUserId(Long userId);
}

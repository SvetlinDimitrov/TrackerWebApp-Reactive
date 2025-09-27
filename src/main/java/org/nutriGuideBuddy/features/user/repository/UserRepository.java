package org.nutriGuideBuddy.features.user.repository;

import java.util.Set;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {

  Mono<User> findByEmail(String email);

  Flux<User> findAllByEmailIn(Set<String> emails);

  Mono<Boolean> existsByEmail(String email);

  Mono<Void> deleteAllByRole(UserRole role);
}

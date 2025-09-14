package org.nutriGuideBuddy.features.user.repository;

import org.nutriGuideBuddy.features.user.dto.UserFilter;
import org.nutriGuideBuddy.features.user.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserCustomRepository {
  Flux<User> findAllByFilter(UserFilter filter);

  Mono<Long> countByFilter(UserFilter filter);
}

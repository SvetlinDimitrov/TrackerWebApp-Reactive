package org.nutriGuideBuddy.features.user.service;

import org.nutriGuideBuddy.features.user.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import reactor.core.publisher.Mono;

public interface UserDetailsService {

  Mono<UserDetailsView> create(Long userId);

  Mono<UserDetailsView> getById(Long id);

  Mono<UserDetailsView> getByUserId(Long userId);

  Mono<UserDetailsView> me();

  Mono<UserDetailsView> update(UserDetailsRequest updateDto, Long id);

  Mono<UserDetailsView> update(UserDetailsRequest updateDto);

  Mono<UserDetails> findByUserIdOrThrow(Long userId);
}

package org.nutriGuideBuddy.features.user_details.service;

import org.nutriGuideBuddy.features.user_details.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
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

package org.nutriGuideBuddy.features.user_details.service;

import org.nutriGuideBuddy.features.user_details.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import reactor.core.publisher.Mono;

public interface UserDetailsService {

  Mono<UserDetailsView> create(String userId);

  Mono<UserDetailsView> getById(String id);

  Mono<UserDetailsView> getByUserId(String userId);

  Mono<UserDetailsView> me();

  Mono<UserDetailsView> update(UserDetailsRequest updateDto, String id);

  Mono<UserDetailsView> update(UserDetailsRequest updateDto);

  Mono<UserDetails> findByUserIdOrThrow(String userId);

  Mono<Void> deleteByUserId(String userId);
}

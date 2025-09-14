package org.nutriGuideBuddy.features.user.service;

import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Flux<UserView> getAll(UserFilter filter);

  Mono<UserView> getById(Long id);

  Mono<UserWithDetailsView> getByIdWithDetails(Long id);

  Mono<UserView> me();

  Mono<UserWithDetailsView> meWithDetails();

  Mono<UserView> create(UserCreateRequest dto, String token);

  Mono<UserView> update(UserUpdateRequest userDto, Long id);

  Mono<Void> delete(Long id);

  Mono<Void> modifyPassword(ChangePasswordRequest dto, String token);

  Mono<User> findByEmailOrThrow(String email);

  Mono<User> findByIOrThrow(Long id);

  Mono<Long> countByFilter(UserFilter filter);

  Mono<Boolean> existsByEmail(String email);
}

package org.nutriGuideBuddy.features.user.service;

import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Flux<UserView> getAll(UserFilter filter);

  Mono<UserView> getById(String id);

  Mono<UserWithDetailsView> getByIdWithDetails(String id);

  Mono<UserView> me();

  Mono<UserWithDetailsView> meWithDetails();

  Mono<UserView> create(UserCreateRequest dto, String token);

  Mono<UserView> update(UserUpdateRequest userDto, String id);

  Mono<Void> delete(String id);

  Mono<Void> modifyPassword(ChangePasswordRequest dto, String token);

  Mono<User> findByEmailOrThrow(String email);

  Mono<User> findByIOrThrow(String id);
}

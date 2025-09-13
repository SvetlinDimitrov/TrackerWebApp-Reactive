package org.nutriGuideBuddy.features.user.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.USER_NOT_FOUND_BY_EMAIL;
import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.USER_NOT_FOUND_BY_ID;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.infrastructure.security.service.JwtEmailVerificationService;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.features.user.entity.UserEntity;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.dto.UserUpdateRequest;
import org.nutriGuideBuddy.features.user.dto.UserView;
import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;
import org.nutriGuideBuddy.infrastructure.mappers.UserMapper;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user_details.service.UserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserService {

  private final JwtEmailVerificationService emailVerificationService;
  private final UserDetailsService userDetailsService;
  private final UserRepository repository;
  private final UserMapper userMapper;

  public Mono<UserView> getById(String id) {
    return findByIOrThrow(id).map(userMapper::toView);
  }

  public Mono<UserWithDetailsView> getByIdWithDetails(String id) {
    return getById(id)
        .zipWith(userDetailsService.getByUserId(id))
        .map(tuple -> userMapper.toViewWithDetails(tuple.getT1(), tuple.getT2()));
  }

  public Mono<UserView> me() {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(userPrincipal -> userMapper.toView(userPrincipal.user()));
  }

  public Mono<UserWithDetailsView> meWithDetails() {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .flatMap(
            principal ->
                userDetailsService
                    .getByUserId(principal.user().getId())
                    .map(
                        details ->
                            userMapper.toViewWithDetails(
                                userMapper.toView(principal.user()), details)));
  }

  public Mono<UserView> create(UserCreateRequest dto, String token) {
    return emailVerificationService
        .validateToken(token)
        .flatMap(
            email ->
                repository
                    .save(userMapper.toEntity(dto, email))
                    .flatMap(user -> userDetailsService.create(user.getId()).thenReturn(user)))
        .map(userMapper::toView);
  }

  public Mono<UserView> update(UserUpdateRequest userDto, String id) {
    return findByIOrThrow(id)
        .flatMap(
            existingUser -> {
              if (existingUser != null) {
                if (!existingUser.getId().equals(id)) {
                  return Mono.error(new ValidationException(Map.of("email", "already in use.")));
                }
                userMapper.update(userDto, existingUser);
                return repository.update(existingUser).map(userMapper::toView);
              }
              return Mono.empty();
            })
        .switchIfEmpty(
            repository
                .findById(id)
                .flatMap(
                    user -> {
                      userMapper.update(userDto, user);
                      return repository.update(user);
                    })
                .map(userMapper::toView));
  }

  public Mono<Void> delete(String id) {
    return repository.deleteUserById(id);
  }

  public Mono<Void> modifyPassword(ChangePasswordRequest dto, String token) {
    return emailVerificationService
        .validateToken(token)
        .flatMap(
            email ->
                findByEmailOrThrow(email)
                    .flatMap(
                        user -> {
                          userMapper.update(dto, user);
                          return repository.save(user);
                        })
                    .then());
  }

  public Mono<UserEntity> findByEmailOrThrow(String email) {
    return repository
        .findByEmail(email)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email))));
  }

  public Mono<UserEntity> findByIOrThrow(String id) {
    return repository
        .findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException(String.format(USER_NOT_FOUND_BY_ID, id))));
  }
}

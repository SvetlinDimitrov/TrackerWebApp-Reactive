package org.nutriGuideBuddy.features.user.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.USER_NOT_FOUND_BY_EMAIL;
import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.USER_NOT_FOUND_BY_ID;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user_details.service.UserDetailsService;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.infrastructure.mappers.UserMapper;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.infrastructure.security.service.JwtEmailVerificationService;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final JwtEmailVerificationService emailVerificationService;
  private final UserDetailsService userDetailsService;
  private final UserRepository repository;
  private final UserMapper userMapper;

  @Override
  public Flux<UserView> getAll(UserFilter filter) {
    return repository.findAllByFilter(filter).map(userMapper::toView);
  }

  @Override
  public Mono<UserView> getById(String id) {
    return findByIOrThrow(id).map(userMapper::toView);
  }

  @Override
  public Mono<UserWithDetailsView> getByIdWithDetails(String id) {
    return getById(id)
        .zipWith(userDetailsService.getByUserId(id))
        .map(tuple -> userMapper.toViewWithDetails(tuple.getT1(), tuple.getT2()));
  }

  @Override
  public Mono<UserView> me() {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(userPrincipal -> userMapper.toView(userPrincipal.user()));
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
  public Mono<Void> delete(String id) {
    return repository.deleteUserById(id).then(userDetailsService.deleteByUserId(id));
  }

  @Override
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

  @Override
  public Mono<User> findByEmailOrThrow(String email) {
    return repository
        .findByEmail(email)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email))));
  }

  @Override
  public Mono<User> findByIOrThrow(String id) {
    return repository
        .findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException(String.format(USER_NOT_FOUND_BY_ID, id))));
  }

  @Override
  public Mono<Long> countByFilter(UserFilter filter) {
    return repository.countByFilter(filter);
  }
}

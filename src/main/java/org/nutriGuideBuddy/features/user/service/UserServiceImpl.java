package org.nutriGuideBuddy.features.user.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.*;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealServiceImpl;
import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.repository.UserCustomRepository;
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
  private final MealServiceImpl mealService;
  private final UserRepository repository;
  private final UserCustomRepository customRepository;
  private final UserMapper userMapper;

  @Override
  public Flux<UserView> getAll(UserFilter filter) {
    return customRepository.findAllByFilter(filter).map(userMapper::toView);
  }

  @Override
  public Mono<Long> countByFilter(UserFilter filter) {
    return customRepository.countByFilter(filter);
  }

  @Override
  public Mono<Boolean> existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public Mono<UserView> getById(Long id) {
    return findByIOrThrow(id).map(userMapper::toView);
  }

  @Override
  public Mono<UserWithDetailsView> getByIdWithDetails(Long id) {
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
  public Mono<UserView> update(UserUpdateRequest userDto, Long id) {
    return findByIOrThrow(id)
        .flatMap(
            existingUser -> {
              if (existingUser != null) {
                if (!existingUser.getId().equals(id)) {
                  return Mono.error(new ValidationException(Map.of("email", "already in use.")));
                }
                userMapper.update(userDto, existingUser);
                return repository.save(existingUser).map(userMapper::toView);
              }
              return Mono.empty();
            });
  }

  @Override
  public Mono<Void> delete(Long id) {
    return mealService.deleteAllByUserId(id)
        .then(repository.deleteById(id));
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
  public Mono<User> findByIOrThrow(Long id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(NOT_FOUND_BY_ID, "User", id))));
  }
}

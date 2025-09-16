package org.nutriGuideBuddy.features.user.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.*;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.repository.UserDetailsRepository;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.mappers.UserDetailsMapper;
import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserDetailsRepository repository;
  private final UserDetailsMapper mapper;
  private final TransactionalOperator operator;

  @Override
  public Mono<UserDetailsView> create(Long userId) {
    UserDetails entity = new UserDetails();
    entity.setUserId(userId);
    return repository.save(entity).map(mapper::toView).as(operator::transactional);
  }

  @Override
  public Mono<UserDetailsView> getById(Long id) {
    return findByIdOrThrow(id).map(mapper::toView);
  }

  @Override
  public Mono<UserDetailsView> getByUserId(Long userId) {
    return findByUserIdOrThrow(userId).map(mapper::toView);
  }

  @Override
  public Mono<UserDetailsView> me() {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(UserPrincipal::details)
        .map(mapper::toView);
  }

  @Override
  public Mono<UserDetailsView> update(UserDetailsRequest updateDto, Long id) {
    return findByIdOrThrow(id)
        .flatMap(
            entity -> {
              mapper.update(updateDto, entity);
              return repository.save(entity);
            })
        .map(mapper::toView)
        .as(operator::transactional);
  }

  @Override
  public Mono<UserDetailsView> update(UserDetailsRequest updateDto) {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(principal -> principal.details().getId())
        .flatMap(userDetailsId -> update(updateDto, userDetailsId));
  }

  @Override
  public Mono<UserDetails> findByUserIdOrThrow(Long userId) {
    return repository
        .findByUserId(userId)
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(String.format(USER_DETAILS_NOT_FOUND_FOR_USER_ID, userId))));
  }

  private Mono<UserDetails> findByIdOrThrow(Long id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(NOT_FOUND_BY_ID, "UserDetails", id))));
  }
}

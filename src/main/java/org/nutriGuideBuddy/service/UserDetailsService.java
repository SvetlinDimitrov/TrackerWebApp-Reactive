package org.nutriGuideBuddy.service;

import static org.nutriGuideBuddy.exceptions.ExceptionMessages.*;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.config.security.UserPrincipal;
import org.nutriGuideBuddy.config.security.service.ReactiveUserDetailsServiceImpl;
import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsRequest;
import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsView;
import org.nutriGuideBuddy.domain.entity.UserDetails;
import org.nutriGuideBuddy.exceptions.NotFoundException;
import org.nutriGuideBuddy.mapper.UserDetailsMapper;
import org.nutriGuideBuddy.repository.UserDetailsRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsService {

  private final UserDetailsRepository repository;
  private final UserDetailsMapper mapper;

  public Mono<UserDetailsView> create(String userId) {
    UserDetails entity = new UserDetails();
    entity.setUserId(userId);
    return repository.save(entity).map(mapper::toView);
  }

  public Mono<UserDetailsView> getById(String id) {
    return findByIdOrThrow(id).map(mapper::toView);
  }

  public Mono<UserDetailsView> getByUserId(String userId) {
    return findByUserIdOrThrow(userId).map(mapper::toView);
  }

  public Mono<UserDetailsView> me() {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(UserPrincipal::details)
        .map(mapper::toView);
  }

  public Mono<UserDetailsView> update(UserDetailsRequest updateDto, String id) {
    return findByIdOrThrow(id)
        .flatMap(
            entity -> {
              mapper.update(updateDto, entity);
              return repository.update(entity.getId(), entity).thenReturn(entity);
            })
        .map(mapper::toView);
  }

  public Mono<UserDetailsView> update(UserDetailsRequest updateDto) {
    return ReactiveUserDetailsServiceImpl.getPrincipal()
        .map(principal -> principal.details().getId())
        .flatMap(userDetailsId -> update(updateDto, userDetailsId));
  }

  public Mono<UserDetails> findByUserIdOrThrow(String userId) {
    return repository
        .findByUserId(userId)
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(String.format(USER_DETAILS_NOT_FOUND_FOR_USER_ID, userId))));
  }

  private Mono<UserDetails> findByIdOrThrow(String id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(new NotFoundException(String.format(USER_DETAILS_NOT_FOUND_BY_ID, id))));
  }
}

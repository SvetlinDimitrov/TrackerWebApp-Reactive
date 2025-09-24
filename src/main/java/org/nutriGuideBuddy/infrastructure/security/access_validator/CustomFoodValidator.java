package org.nutriGuideBuddy.infrastructure.security.access_validator;

import static org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl.getPrincipalId;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.service.CustomFoodServiceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomFoodValidator {

  private final CustomFoodServiceImpl service;

  public Mono<Void> validateFood(Long id) {
    return getPrincipalId()
        .flatMap(userId -> service.existsByIdAndUserId(id, userId))
        .flatMap(
            hasAccess -> {
              if (!hasAccess) {
                return Mono.error(new AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }
}

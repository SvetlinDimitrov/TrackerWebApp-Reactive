package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.service.CustomFoodService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomFoodValidator extends AbstractAccessValidator {

  private final CustomFoodService service;

  /** Owner-only: custom food must belong to the authenticated user. */
  public Mono<Void> validateFoodAccess(Long foodId) {
    return currentUserId()
        .flatMap(userId -> service.existsByIdAndUserId(foodId, userId))
        .flatMap(
            has -> has ? Mono.empty() : Mono.error(new AccessDeniedException("Access denied")));
  }
}

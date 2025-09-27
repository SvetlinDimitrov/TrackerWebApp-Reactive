package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MealAccessValidator extends AbstractAccessValidator {

  private final MealService service;

  /** Owner-only: meal must belong to the authenticated user. */
  public Mono<Void> validateMealAccess(Long mealId) {
    return currentUserId()
        .flatMap(userId -> service.existsByIdAndUserId(mealId, userId))
        .flatMap(
            has -> has ? Mono.empty() : Mono.error(new AccessDeniedException("Access denied")));
  }
}

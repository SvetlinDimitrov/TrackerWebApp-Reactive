package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MealFoodAccessValidator extends AbstractAccessValidator {

  private final MealFoodService mealFoodService;

  /** Ensures the given food belongs to the given meal. */
  public Mono<Void> validateFoodAccess(Long mealId, Long foodId) {
    return mealFoodService
        .existsByIdAndMealId(foodId, mealId)
        .flatMap(
            hasAccess ->
                hasAccess ? Mono.empty() : Mono.error(new AccessDeniedException("Access denied")));
  }
}

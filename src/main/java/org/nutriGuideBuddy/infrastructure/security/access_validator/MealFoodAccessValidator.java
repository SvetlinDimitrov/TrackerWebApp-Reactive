package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MealFoodAccessValidator {

  private final MealFoodService mealFoodService;

  public Mono<Void> validateFood(Long mealId, Long foodId) {
    return mealFoodService
        .existsByIdAndMealId(foodId, mealId)
        .flatMap(
            hasAccess -> {
              if (!hasAccess) {
                return Mono.error(new AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }
}

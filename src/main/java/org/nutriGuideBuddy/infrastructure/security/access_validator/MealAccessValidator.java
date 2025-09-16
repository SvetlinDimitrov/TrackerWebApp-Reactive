package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.service.MealFoodServiceImp;
import org.nutriGuideBuddy.features.meal.service.MealServiceImpl;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MealAccessValidator {

  private final MealServiceImpl service;
  private final MealFoodServiceImp mealFoodService;

  public Mono<Void> validateAccess(Long id) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> service.existsByIdAndUserId(id, userId))
        .flatMap(
            hasAccess -> {
              if (!hasAccess) {
                return Mono.error(new AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }

  public Mono<Void> validateFood(Long mealId, Long foodId) {
    return mealFoodService
        .existsByIdAndMealId(foodId, mealId)
        .flatMap(
            hasAccess -> {
              if (!hasAccess) {
                return Mono.error(new java.nio.file.AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }
}

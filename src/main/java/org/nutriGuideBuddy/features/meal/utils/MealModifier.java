package org.nutriGuideBuddy.features.meal.utils;

import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.dto.CreateMeal;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.food.utils.Validator;
import reactor.core.publisher.Mono;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove.INVALID_STRING_LENGTH_MESSAGE;

public class MealModifier {

  public static Mono<Meal> validateAndUpdateEntity(CreateMeal dto, String userId) {
    Meal entity = new Meal();
    entity.setUserId(userId);
    return validateAndUpdateName(entity, dto);
  }

  public static Mono<Meal> validateAndUpdateEntity(Meal entity, CreateMeal dto) {
    return validateAndUpdateName(entity, dto);
  }

  private static Mono<Meal> validateAndUpdateName(Meal entity, CreateMeal dto) {
    return Mono.just(entity)
        .filter(u -> dto.name() != null)
        .flatMap(
            u -> {
              if (Validator.validateString(dto.name(), 1, 255)) {
                u.setName(dto.name());
                return Mono.just(u);
              } else {
                return Mono.error(
                    new BadRequestException(INVALID_STRING_LENGTH_MESSAGE + "for name length."));
              }
            })
        .switchIfEmpty(Mono.just(entity));
  }
}

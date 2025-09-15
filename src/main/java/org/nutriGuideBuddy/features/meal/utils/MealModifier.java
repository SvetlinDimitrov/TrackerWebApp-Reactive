package org.nutriGuideBuddy.features.meal.utils;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove.INVALID_STRING_LENGTH_MESSAGE;

import org.nutriGuideBuddy.features.food.utils.Validator;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import reactor.core.publisher.Mono;

public class MealModifier {

  public static Mono<Meal> validateAndUpdateEntity(MealCreateRequest dto, Long userId) {
    Meal entity = new Meal();
    entity.setUserId(userId);
    return validateAndUpdateName(entity, dto);
  }

  public static Mono<Meal> validateAndUpdateEntity(Meal entity, MealCreateRequest dto) {
    return validateAndUpdateName(entity, dto);
  }

  private static Mono<Meal> validateAndUpdateName(Meal entity, MealCreateRequest dto) {
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

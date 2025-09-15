package org.nutriGuideBuddy.features.meal.utils;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove.INVALID_STRING_LENGTH_MESSAGE;

import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import org.nutriGuideBuddy.features.food.entity.Food;
import org.nutriGuideBuddy.features.food.utils.Validator;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import reactor.core.publisher.Mono;

public class FoodModifier {

  public static Mono<Food> validateAndUpdateEntity(Food entity, InsertFoodDto dto) {
    return validateAndUpdateName(entity, dto);
  }

  private static Mono<Food> validateAndUpdateName(Food entity, InsertFoodDto dto) {
    return Mono.just(entity)
        .filter(u -> Validator.validateString(dto.name(), 1, 255))
        .flatMap(
            u -> {
              u.setName(dto.name());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(new BadRequestException(INVALID_STRING_LENGTH_MESSAGE + " for food name")));
  }
}

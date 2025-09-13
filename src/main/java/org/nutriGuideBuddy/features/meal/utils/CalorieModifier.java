package org.nutriGuideBuddy.features.meal.utils;

import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.entity.Calorie;
import org.nutriGuideBuddy.features.food.dto.CalorieView;
import org.nutriGuideBuddy.features.food.enums.AllowedCalorieUnits;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove;
import org.nutriGuideBuddy.features.food.utils.Validator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class CalorieModifier {

  public static Mono<Calorie> validateAndUpdateEntity(Calorie entity, CalorieView dto) {
    return Mono.just(entity)
        .flatMap(data -> validateAndUpdateSize(data, dto))
        .flatMap(data -> validateAndUpdateUnit(data, dto));
  }

  private static Mono<Calorie> validateAndUpdateUnit(Calorie entity, CalorieView dto) {
    return Mono.just(entity)
        .filter(
            u ->
                Validator.validateString(dto.unit(), 1, 255)
                    && AllowedCalorieUnits.CALORIE.getSymbol().equals(dto.unit()))
        .flatMap(
            u -> {
              u.setUnit(dto.unit());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    "Invalid calorie unit. Valid ones : "
                        + AllowedCalorieUnits.CALORIE.getSymbol())));
  }

  private static Mono<Calorie> validateAndUpdateSize(Calorie entity, CalorieView dto) {
    return Mono.just(entity)
        .filter(u -> Validator.validateBigDecimal(dto.amount(), BigDecimal.ZERO))
        .flatMap(
            u -> {
              u.setAmount(dto.amount());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    ExceptionMessagesToRemove.INVALID_NUMBER_MESSAGE
                        + " Calorie amount , must be positive number")));
  }
}

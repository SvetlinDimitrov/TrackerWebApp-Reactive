package org.nutriGuideBuddy.features.meal.utils;

import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.dto.NutritionView;
import org.nutriGuideBuddy.features.food.entity.Nutrition;
import org.nutriGuideBuddy.features.food.enums.AllowedNutrients;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove;
import org.nutriGuideBuddy.features.food.utils.Validator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;

public class NutritionModifier {

  public static Mono<Nutrition> validateAndUpdateEntity(
      NutritionView dto, String foodId, String userId) {

    Nutrition entity = new Nutrition();
    entity.setUserId(userId);
    entity.setFoodId(foodId);

    return Mono.just(entity)
        .flatMap(data -> validateAndUpdateName(data, dto))
        .flatMap(data -> validateAndUpdateUnit(data, dto))
        .flatMap(data -> validateAndUpdateAmount(data, dto));
  }

  private static Mono<Nutrition> validateAndUpdateName(Nutrition entity, NutritionView dto) {
    return Mono.just(entity)
        .filter(
            u ->
                dto.name() != null
                    && Arrays.stream(AllowedNutrients.values())
                        .anyMatch(
                            correctValue -> correctValue.getNutrientName().equals(dto.name())))
        .flatMap(
            u -> {
              u.setName(dto.name());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(new BadRequestException("Invalid nutrition name: " + dto.name())));
  }

  private static Mono<Nutrition> validateAndUpdateUnit(Nutrition entity, NutritionView dto) {
    return Mono.just(entity)
        .filter(
            u ->
                dto.name() != null
                    && dto.unit() != null
                    && Arrays.stream(AllowedNutrients.values())
                        .anyMatch(
                            correctValue ->
                                correctValue.getNutrientName().equals(dto.name())
                                    && correctValue.getNutrientUnit().equals(dto.unit())))
        .flatMap(
            u -> {
              u.setUnit(dto.unit());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    "Invalid nutrition unit: " + dto.unit() + " for name : " + dto.name())));
  }

  private static Mono<Nutrition> validateAndUpdateAmount(Nutrition entity, NutritionView dto) {
    return Mono.just(entity)
        .filter(data -> Validator.validateBigDecimal(dto.amount(), BigDecimal.ZERO))
        .flatMap(
            u -> {
              u.setAmount(dto.amount());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    ExceptionMessagesToRemove.INVALID_NUMBER_MESSAGE.getMessage()
                        + "for nutrition amount , must be higher than 0")));
  }
}

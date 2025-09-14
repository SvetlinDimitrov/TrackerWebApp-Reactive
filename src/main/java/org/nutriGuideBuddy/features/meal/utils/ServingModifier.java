package org.nutriGuideBuddy.features.meal.utils;

import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.entity.Serving;
import org.nutriGuideBuddy.features.food.dto.ServingView;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessagesToRemove;
import org.nutriGuideBuddy.features.food.utils.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ServingModifier {

  public static Mono<Serving> validateAndUpdateMainEntity(ServingView dto, Long foodId) {
    return validateAndUpdateAmount(new Serving(), dto)
        .flatMap(data -> validateAndUpdateWeight(data, dto))
        .flatMap(data -> validateAndUpdateMetric(data, dto))
        .map(
            data -> {
              data.setFoodId(foodId);
              data.setMain(true);
              return data;
            });
  }

  public static Mono<List<Serving>> validateAndUpdateListOfEntities(
      List<ServingView> dto, Long foodId) {
    if (dto == null) {
      return Mono.error(new BadRequestException("alternative servings cannot be null"));
    }
    List<Mono<Serving>> monoList =
        dto.stream()
            .map(
                view ->
                    validateAndUpdateAmount(new Serving(), view)
                        .flatMap(data -> validateAndUpdateWeight(data, view))
                        .flatMap(data -> validateAndUpdateMetric(data, view))
                        .map(
                            data -> {
                              data.setFoodId(foodId);
                              data.setMain(false);
                              return data;
                            }))
            .collect(Collectors.toList());

    return Flux.fromIterable(monoList).flatMap(mono -> mono).collectList();
  }

  private static Mono<Serving> validateAndUpdateMetric(Serving entity, ServingView dto) {
    return Mono.just(entity)
        .filter(u -> Validator.validateString(dto.metric(), 1, 255))
        .flatMap(
            u -> {
              u.setMetric(dto.metric());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    ExceptionMessagesToRemove.INVALID_STRING_LENGTH_MESSAGE.getMessage()
                        + "for food metric.")));
  }

  private static Mono<Serving> validateAndUpdateWeight(Serving entity, ServingView dto) {
    return Mono.just(entity)
        .filter(u -> Validator.validateBigDecimal(dto.servingWeight(), BigDecimal.ZERO))
        .flatMap(
            u -> {
              u.setServingWeight(dto.servingWeight());
              return Mono.just(u);
            })
        .switchIfEmpty(
            Mono.error(
                new BadRequestException(
                    ExceptionMessagesToRemove.INVALID_NUMBER_MESSAGE.getMessage()
                        + "for food size weight , must be greater then 0")));
  }

  private static Mono<Serving> validateAndUpdateAmount(Serving entity, ServingView dto) {
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
                    ExceptionMessagesToRemove.INVALID_NUMBER_MESSAGE.getMessage()
                        + "for food amount , must be greater then 0")));
  }
}

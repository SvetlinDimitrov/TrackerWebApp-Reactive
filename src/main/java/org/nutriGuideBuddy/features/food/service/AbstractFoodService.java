package org.nutriGuideBuddy.features.food.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.utils.*;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.dto.*;
import org.nutriGuideBuddy.features.food.entity.*;
import org.nutriGuideBuddy.features.food.repository.FoodRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

@Service
@RequiredArgsConstructor
public abstract class AbstractFoodService {

  protected final FoodRepository repository;

  protected Mono<FoodView> toFoodView(Food entity, String mealId) {
    return Mono.zip(
            repository.findCalorieByFoodId(entity.getId(), mealId),
            repository.findAllNutritionsByFoodId(entity.getId()).collectList(),
            repository.findAllServingsByFoodId(entity.getId()).collectList(),
            repository.findFoodInfoByFoodId(entity.getId()))
        .flatMap(
            tuple ->
                Mono.zip(
                    Mono.just(CalorieView.toView(tuple.getT1())),
                    Mono.just(tuple.getT2().stream().map(NutritionView::toView).toList()),
                    Mono.just(
                        tuple.getT3().stream()
                            .filter(serving -> !serving.getMain())
                            .map(ServingView::toView)
                            .toList()),
                    Mono.just(
                        Objects.requireNonNull(
                            tuple.getT3().stream()
                                .filter(Serving::getMain)
                                .findFirst()
                                .map(ServingView::toView)
                                .orElse(null))),
                    Mono.just(FoodInfoView.toView(tuple.getT4()))))
        .map(
            tuple ->
                new FoodView(
                    entity.getId(),
                    entity.getName(),
                    tuple.getT5(),
                    tuple.getT4(),
                    tuple.getT3(),
                    tuple.getT1(),
                    tuple.getT2()));
  }

  protected Mono<ShortenFood> toShortenFoodView(Food entity, String mealId) {
    return repository
        .findCalorieByFoodId(entity.getId(), mealId)
        .map(calories -> new ShortenFood(entity.getId(), entity.getName(), calories.getAmount()));
  }

  protected Mono<Food> getFoodEntityByIdMealIdUserId(String foodId, String mealId, String userId) {
    return repository
        .findFoodByIdAndMealIdAndUserId(foodId, mealId, userId)
        .switchIfEmpty(Mono.error(new BadRequestException("No food found with id: " + foodId)));
  }

  protected Mono<Tuple5<Food, List<Serving>, Calorie, List<Nutrition>, FoodInfo>> createAndGetFood(
      String userId, InsertFoodDto dto, String mealId) {
    return createAndFillFoodEntity(dto, userId, mealId)
        .flatMap(
            food ->
                Mono.zip(
                    Mono.just(food),
                    createAndFillServings(dto.mainServing(), dto.otherServing(), food.getId()),
                    createAndFillCalorieEntity(
                        dto.calories(),
                        food.getId(),
                        mealId,
                        Optional.ofNullable(mealId).map((a) -> userId).orElse(null)),
                    createAndFillNutritions(
                        dto.nutrients(),
                        food.getId(),
                        Optional.ofNullable(mealId).map((a) -> userId).orElse(null)),
                    createAndFillFoodInfoEntity(dto.foodDetails(), food.getId())));
  }

  protected Mono<Void> saveFoodEntity(
      Food food,
      List<Serving> servingEntities,
      Calorie calorie,
      List<Nutrition> nutritionEntities,
      FoodInfo foodInfo) {
    return repository
        .saveFood(food)
        .then(repository.saveCalorie(calorie))
        .thenMany(repository.saveAllServings(servingEntities))
        .thenMany(repository.saveAllNutritions(nutritionEntities))
        .then(repository.saveFoodInfo(foodInfo))
        .then();
  }

  private Mono<Food> createAndFillFoodEntity(InsertFoodDto dto, String userId, String mealId) {
    if (dto == null) {
      return Mono.error(new BadRequestException("food cannot be null"));
    }
    return Mono.just(new Food())
        .flatMap(food -> FoodModifier.validateAndUpdateEntity(food, dto))
        .flatMap(
            food -> {
              food.setUserId(userId);
              Optional.ofNullable(mealId).ifPresent(food::setMealId);
              return Mono.just(food);
            });
  }

  private Mono<List<Serving>> createAndFillServings(
      ServingView mainServing, List<ServingView> others, String foodId) {
    if (mainServing == null) {
      return Mono.error(new BadRequestException("Main serving cannot be null"));
    }
    return ServingModifier.validateAndUpdateMainEntity(mainServing, foodId)
        .zipWith(ServingModifier.validateAndUpdateListOfEntities(others, foodId))
        .map(
            data -> {
              ArrayList<Serving> result = new ArrayList<>();
              result.add(data.getT1());
              result.addAll(data.getT2());
              return result;
            });
  }

  private Mono<Calorie> createAndFillCalorieEntity(
      CalorieView dto, String foodId, String mealId, String userId) {
    if (dto == null) {
      return Mono.error(new BadRequestException("calorie cannot be null"));
    }
    return Mono.just(new Calorie())
        .flatMap(
            entity -> {
              entity.setFoodId(foodId);
              entity.setUserId(userId);
              entity.setMealId(mealId);
              return Mono.just(entity);
            })
        .flatMap(calorieEntity -> CalorieModifier.validateAndUpdateEntity(calorieEntity, dto));
  }

  private Mono<FoodInfo> createAndFillFoodInfoEntity(FoodInfoView dto, String foodId) {
    FoodInfo newEntity = new FoodInfo();
    newEntity.setFoodId(foodId);

    if (dto == null) {
      return Mono.just(newEntity);
    }
    return Mono.just(newEntity)
        .flatMap(
            entity -> {
              entity.setFoodId(foodId);
              return Mono.just(entity);
            })
        .flatMap(calorieEntity -> FoodInfoModifier.validateAndUpdateEntity(calorieEntity, dto));
  }

  private Mono<List<Nutrition>> createAndFillNutritions(
      List<NutritionView> dtoList, String foodId, String userId) {
    if (dtoList == null) {
      return Mono.error(new BadRequestException("nutrients cannot be null"));
    }
    return Flux.fromIterable(dtoList)
        .flatMap(dto -> NutritionModifier.validateAndUpdateEntity(dto, foodId, userId))
        .collectList();
  }
}

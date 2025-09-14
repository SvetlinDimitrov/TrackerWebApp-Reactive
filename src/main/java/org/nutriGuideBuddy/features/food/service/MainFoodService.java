package org.nutriGuideBuddy.features.food.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.dto.*;
import org.nutriGuideBuddy.features.food.entity.*;
import org.nutriGuideBuddy.features.food.repository.*;
import org.nutriGuideBuddy.features.meal.utils.*;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

@Service
@RequiredArgsConstructor
public class MainFoodService {

  private final CalorieRepository calorieRepository;
  private final FoodInfoRepository foodInfoRepository;
  private final ServingRepository servingRepository;
  private final NutrientRepository nutrientRepository;
  private final FoodRepository foodRepository;

  public Mono<FoodView> toFoodView(Food entity, Long mealId) {
    return Mono.zip(
            calorieRepository.findByFoodIdAndMealId(entity.getId(), mealId),
            nutrientRepository.findAllByFoodId(entity.getId()).collectList(),
            servingRepository.findAllByFoodId(entity.getId()).collectList(),
            foodInfoRepository.findByFoodId(entity.getId()))
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

  public Mono<FoodShortView> toShortenFoodView(Food entity, Long mealId) {
    return calorieRepository
        .findByFoodIdAndMealId(entity.getId(), mealId)
        .map(calories -> new FoodShortView(entity.getId(), entity.getName(), calories.getAmount()));
  }

  public Mono<Food> getFoodEntityByIdMealIdUserId(Long foodId, Long mealId, Long userId) {
    return foodRepository
        .findByIdAndMealIdAndUserId(foodId, mealId, userId)
        .switchIfEmpty(Mono.error(new BadRequestException("No food found with id: " + foodId)));
  }

  public Mono<Tuple5<Food, List<Serving>, Calorie, List<Nutrition>, FoodInfo>> createAndGetFood(
      Long userId, InsertFoodDto dto, Long mealId) {
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

  public Mono<Void> saveFoodEntity(
      Food food,
      List<Serving> servingEntities,
      Calorie calorie,
      List<Nutrition> nutritionEntities,
      FoodInfo foodInfo) {
    return foodRepository
        .save(food)
        .then(calorieRepository.save(calorie))
        .thenMany(servingRepository.saveAll(servingEntities))
        .thenMany(nutrientRepository.saveAll(nutritionEntities))
        .then(foodInfoRepository.save(foodInfo))
        .then();
  }

  private Mono<Food> createAndFillFoodEntity(InsertFoodDto dto, Long userId, Long mealId) {
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
      ServingView mainServing, List<ServingView> others, Long foodId) {
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
      CalorieView dto, Long foodId, Long mealId, Long userId) {
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

  private Mono<FoodInfo> createAndFillFoodInfoEntity(FoodInfoView dto, Long foodId) {
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
      List<NutritionView> dtoList, Long foodId, Long userId) {
    if (dtoList == null) {
      return Mono.error(new BadRequestException("nutrients cannot be null"));
    }
    return Flux.fromIterable(dtoList)
        .flatMap(dto -> NutritionModifier.validateAndUpdateEntity(dto, foodId, userId))
        .collectList();
  }
}

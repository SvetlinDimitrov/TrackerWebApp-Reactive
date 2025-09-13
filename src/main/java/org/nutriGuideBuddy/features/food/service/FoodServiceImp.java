package org.nutriGuideBuddy.features.food.service;

import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.repository.FoodRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FoodServiceImp extends AbstractFoodService {

  public FoodServiceImp(FoodRepository repository) {
    super(repository);
  }

  public Mono<Void> deleteFoodById(String mealId, String foodId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> getFoodEntityByIdMealIdUserId(foodId, mealId, userId))
        .flatMap(food -> repository.deleteFoodById(food.getId(), food.getMealId()));
  }

  public Mono<Void> addFoodToMeal(InsertFoodDto dto, String mealId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> repository.findMealByIdAndUserId(mealId, userId))
        .switchIfEmpty(Mono.error(new BadRequestException("No meal found with id: " + mealId)))
        .flatMap(mealEntity -> createAndGetFood(mealEntity.getUserId(), dto, mealEntity.getId()))
        .flatMap(
            data ->
                saveFoodEntity(
                    data.getT1(), data.getT2(), data.getT3(), data.getT4(), data.getT5()));
  }

  public Mono<Void> changeFood(String mealId, String foodId, InsertFoodDto dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> getFoodEntityByIdMealIdUserId(foodId, mealId, userId))
        .flatMap(food -> createAndGetFood(food.getUserId(), dto, mealId))
        .flatMap(
            data ->
                repository
                    .deleteFoodById(foodId, mealId)
                    .then(
                        saveFoodEntity(
                            data.getT1(), data.getT2(), data.getT3(), data.getT4(), data.getT5())));
  }
}

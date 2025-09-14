package org.nutriGuideBuddy.features.food.service;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import org.nutriGuideBuddy.features.food.repository.FoodRepository;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FoodServiceImp {

  private final MainFoodService mainFoodService;
  private final FoodRepository foodRepository;
  private final MealRepository mealRepository;

  public Mono<Void> deleteFoodById(Long mealId, Long foodId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mainFoodService.getFoodEntityByIdMealIdUserId(foodId, mealId, userId))
        .flatMap(food -> foodRepository.deleteById(food.getId()));
  }

  public Mono<Void> addFoodToMeal(InsertFoodDto dto, Long mealId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mealRepository.findByIdAndUserId(mealId, userId))
        .switchIfEmpty(Mono.error(new BadRequestException("No meal found with id: " + mealId)))
        .flatMap(
            mealEntity ->
                mainFoodService.createAndGetFood(mealEntity.getUserId(), dto, mealEntity.getId()))
        .flatMap(
            data ->
                mainFoodService.saveFoodEntity(
                    data.getT1(), data.getT2(), data.getT3(), data.getT4(), data.getT5()));
  }

  public Mono<Void> changeFood(Long mealId, Long foodId, InsertFoodDto dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mainFoodService.getFoodEntityByIdMealIdUserId(foodId, mealId, userId))
        .flatMap(food -> mainFoodService.createAndGetFood(food.getUserId(), dto, mealId))
        .flatMap(
            data ->
                foodRepository
                    .deleteById(foodId)
                    .then(
                        mainFoodService.saveFoodEntity(
                            data.getT1(), data.getT2(), data.getT3(), data.getT4(), data.getT5())));
  }
}

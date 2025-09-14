package org.nutriGuideBuddy.web;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import org.nutriGuideBuddy.features.food.service.FoodServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals/{mealId}/insertFood")
public class FoodController {

  private final FoodServiceImp service;

  @PostMapping
  public Mono<Void> addFood(@RequestBody InsertFoodDto dto, @PathVariable Long mealId) {
    return service.addFoodToMeal(dto, mealId);
  }

  @DeleteMapping("/{foodId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFoodFromMeal(@PathVariable Long mealId, @PathVariable Long foodId) {
    return service.deleteFoodById(mealId, foodId);
  }

  @PutMapping("/{foodId}")
  public Mono<Void> changeFood(
      @PathVariable Long mealId, @PathVariable Long foodId, @RequestBody InsertFoodDto dto) {
    return service.changeFood(mealId, foodId, dto);
  }
}

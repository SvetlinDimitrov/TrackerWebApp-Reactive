package org.nutriGuideBuddy.web;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionResponse;
import org.nutriGuideBuddy.features.food.service.FoodServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals/{mealId}/insertFood")
public class FoodController {

  private final FoodServiceImp service;

  @PostMapping
  private Mono<Void> addFood(@RequestBody InsertFoodDto dto, @PathVariable String mealId) {
    return service.addFoodToMeal(dto, mealId);
  }

  @DeleteMapping("/{foodId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  private Mono<Void> deleteFoodFromMeal(@PathVariable String mealId, @PathVariable String foodId) {
    return service.deleteFoodById(mealId, foodId);
  }

  @PutMapping("/{foodId}")
  private Mono<Void> changeFood(
      @PathVariable String mealId, @PathVariable String foodId, @RequestBody InsertFoodDto dto) {
    return service.changeFood(mealId, foodId, dto);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<ExceptionResponse> catchUserNotFound(BadRequestException e) {
    return Mono.just(new ExceptionResponse(e.getMessage()));
  }
}

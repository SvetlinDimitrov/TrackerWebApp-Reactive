package org.nutriGuideBuddy.infrastructure.nutritionx_api.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.dto.InsertFoodDto;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.ListFoodsResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.service.NutritionixApiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food_db_api/search")
public class NutritionixApiController {

  private final NutritionixApiService service;

  // Mostly return a single food
  @GetMapping("/common/{term}")
  public Mono<List<InsertFoodDto>> getFoodBySearchCriteria(@PathVariable String term) {
    return service.getCommonFoodBySearchTerm(term);
  }

  // Mostly return a single food
  @GetMapping("/branded/{id}")
  public Mono<List<InsertFoodDto>> getBrandedFoodById(@PathVariable String id) {
    return service.getBrandedFoodById(id);
  }

  @GetMapping
  public Mono<ListFoodsResponse> getFoodsByName(@RequestParam String foodName) {
    return service.getAllFoodsByFoodName(foodName);
  }

  //  @GetMapping("/{foodId}")
  //  public List<InsertFoodDto> getFoodById(@PathVariable String foodId){
  //
  //  }
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<ExceptionResponse> catchUserNotFound(BadRequestException e) {
    return Mono.just(new ExceptionResponse(e.getMessage()));
  }
}

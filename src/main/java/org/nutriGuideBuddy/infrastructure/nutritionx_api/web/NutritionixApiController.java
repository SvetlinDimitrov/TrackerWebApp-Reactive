package org.nutriGuideBuddy.infrastructure.nutritionx_api.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.ListFoodsResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.service.NutritionixApiService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/food_db_api/search")
public class NutritionixApiController {

  private final NutritionixApiService service;
  private final UserDetailsAccessValidator userDetailsAccessValidator;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Mono<ListFoodsResponse> getAllByName(@RequestParam String foodName) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(service.getAllFoodsByFoodName(foodName));
  }

  @GetMapping("/common")
  @ResponseStatus(HttpStatus.OK)
  public Mono<List<FoodCreateRequest>> getFoodBySearchCriteria(@RequestParam String term) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(service.getCommonFoodBySearchTerm(term));
  }

  @GetMapping("/branded/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<List<FoodCreateRequest>> getBrandedFoodById(@PathVariable String id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(service.getBrandedFoodById(id));
  }
}

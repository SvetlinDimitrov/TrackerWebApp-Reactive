package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.*;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import org.nutriGuideBuddy.infrastructure.security.access_validator.MealAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.MealFoodAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meals/{mealId}/food")
public class MealFoodController {

  private final MealFoodService mealFoodService;
  private final MealFoodAccessValidator validator;
  private final MealAccessValidator mealValidator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<MealFoodView> getAllFood(
      @PathVariable Long mealId, @RequestBody(required = false) @Valid MealFoodFilter filter) {
    return validator
        .validateFullyRegistered()
        .then(mealValidator.validateMealAccess(mealId))
        .thenMany(mealFoodService.getAll(mealId, filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> countByMealIdAndFilter(
      @PathVariable Long mealId, @RequestBody(required = false) @Valid MealFoodFilter filter) {
    return validator
        .validateFullyRegistered()
        .then(mealValidator.validateMealAccess(mealId))
        .then(mealFoodService.countByMealIdAndFilter(mealId, filter));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MealFoodView> createFood(
      @RequestBody @Valid FoodCreateRequest dto, @PathVariable Long mealId) {
    return validator
        .validateFullyRegistered()
        .then(mealValidator.validateMealAccess(mealId))
        .then(mealFoodService.create(dto, mealId));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<MealFoodView> getFood(@PathVariable Long mealId, @PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(mealId, id))
        .then(mealFoodService.getById(id));
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<MealFoodView> updateFood(
      @RequestBody @Valid FoodUpdateRequest dto, @PathVariable Long mealId, @PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(mealId, id))
        .then(mealFoodService.update(dto, id, mealId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFood(@PathVariable Long mealId, @PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(mealId, id))
        .then(mealFoodService.delete(id, mealId));
  }
}

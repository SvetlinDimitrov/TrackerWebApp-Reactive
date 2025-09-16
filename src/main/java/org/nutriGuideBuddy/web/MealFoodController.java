package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealFoodCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.dto.MealFoodUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodView;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.MealAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.MealFoodAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meals/{mealId}/food")
public class MealFoodController {

  private final MealFoodService mealFoodService;
  private final MealAccessValidator mealAccessValidator;
  private final MealFoodAccessValidator mealFoodAccessValidator;
  private final UserDetailsAccessValidator userDetailsAccessValidator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<MealFoodView> getAllFood(
      @PathVariable Long mealId, @RequestBody @Valid MealFoodFilter filter) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateAccess(mealId))
        .thenMany(mealFoodService.getAll(mealId, filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> countByMealIdAndFilter(
      @PathVariable Long mealId, @RequestBody @Valid MealFoodFilter filter) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateAccess(mealId))
        .then(mealFoodService.countByMealIdAndFilter(mealId, filter));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MealFoodView> createFood(
      @RequestBody @Valid MealFoodCreateRequest dto, @PathVariable Long mealId) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateAccess(mealId))
        .then(mealFoodService.create(dto, mealId));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<MealFoodView> getFood(@PathVariable Long mealId, @PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealFoodAccessValidator.validateFood(mealId, id))
        .then(mealFoodService.getById(id));
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<MealFoodView> updateFood(
      @RequestBody @Valid MealFoodUpdateRequest dto,
      @PathVariable Long mealId,
      @PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealFoodAccessValidator.validateFood(mealId, id))
        .then(mealFoodService.update(dto, id, mealId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFood(@PathVariable Long mealId, @PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealFoodAccessValidator.validateFood(mealId, id))
        .then(mealFoodService.delete(id, mealId));
  }
}

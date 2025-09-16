package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.*;
import org.nutriGuideBuddy.features.meal.service.MealFoodServiceImp;
import org.nutriGuideBuddy.features.meal.service.MealServiceImpl;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.infrastructure.security.access_validator.MealAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meals")
public class MealController {

  private final MealServiceImpl service;
  private final MealFoodServiceImp mealFoodService;
  private final MealAccessValidator mealAccessValidator;
  private final UserDetailsAccessValidator userDetailsAccessValidator;
  private final UserAccessValidator userAccessValidator;

  @PostMapping("/get-all")
  public Flux<MealView> getAll(@RequestBody @Valid MealFilter filter) {
    return userDetailsAccessValidator.validateFullyRegistered().thenMany(service.getAll(filter));
  }

  @PostMapping("/get-all/count")
  public Mono<Long> count(@RequestBody @Valid MealFilter filter) {
    return userDetailsAccessValidator.validateFullyRegistered().then(service.count(filter));
  }

  @PostMapping("/{mealId}")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MealFoodView> createFood(
      @RequestBody @Valid MealFoodCreateRequest dto, @PathVariable Long mealId) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateAccess(mealId))
        .then(mealFoodService.create(dto, mealId));
  }

  @GetMapping("/{id}")
  public Mono<MealView> get(@PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(
            userAccessValidator
                .hasRole(UserRole.ADMIN)
                .flatMap(
                    isAdmin -> isAdmin ? Mono.empty() : mealAccessValidator.validateAccess(id)))
        .then(service.getById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MealView> create(@RequestBody @Valid MealCreateRequest dto) {
    return service.create(dto);
  }

  @PatchMapping("/{id}")
  public Mono<MealView> update(@RequestBody @Valid MealUpdateRequest dto, @PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(
            userAccessValidator
                .hasRole(UserRole.ADMIN)
                .flatMap(
                    isAdmin -> isAdmin ? Mono.empty() : mealAccessValidator.validateAccess(id)))
        .then(service.updateById(dto, id));
  }

  @PatchMapping("/meals/{id}/food/{foodId}")
  public Mono<MealFoodView> updateFood(
      @RequestBody @Valid MealFoodUpdateRequest dto,
      @PathVariable Long id,
      @PathVariable Long foodId) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateFood(id, foodId))
        .then(mealFoodService.update(dto, foodId, id));
  }

  @DeleteMapping("/{id}")
  public Mono<Void> delete(@PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(
            userAccessValidator
                .hasRole(UserRole.ADMIN)
                .flatMap(
                    isAdmin -> isAdmin ? Mono.empty() : mealAccessValidator.validateAccess(id)))
        .then(service.deleteById(id));
  }

  @DeleteMapping("/meals/{id}/food/{foodId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFood(@PathVariable Long id, @PathVariable Long foodId) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(mealAccessValidator.validateFood(id, foodId))
        .then(mealFoodService.delete(foodId, id));
  }
}

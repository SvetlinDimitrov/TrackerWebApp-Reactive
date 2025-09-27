package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodView;
import org.nutriGuideBuddy.features.custom_food.service.CustomFoodServiceImpl;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.FoodUpdateRequest;
import org.nutriGuideBuddy.infrastructure.security.access_validator.CustomFoodValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/custom-foods")
public class CustomFoodController {

  private final CustomFoodServiceImpl service;
  private final CustomFoodValidator validator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<CustomFoodView> getAllFood(
      @RequestBody(required = false) @Valid CustomFoodFilter filter) {
    return validator.validateFullyRegistered().thenMany(service.getAll(filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> countByFilter(@RequestBody(required = false) @Valid CustomFoodFilter filter) {
    return validator.validateFullyRegistered().then(service.countByFilter(filter));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<CustomFoodView> create(@RequestBody @Valid FoodCreateRequest dto) {
    return validator.validateFullyRegistered().then(service.create(dto));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<CustomFoodView> getFood(@PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(id))
        .then(service.getById(id));
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<CustomFoodView> update(
      @RequestBody @Valid FoodUpdateRequest dto, @PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(id))
        .then(service.update(dto, id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateFoodAccess(id))
        .then(service.delete(id));
  }
}

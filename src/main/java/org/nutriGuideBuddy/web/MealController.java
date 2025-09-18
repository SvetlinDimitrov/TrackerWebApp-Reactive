package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.*;
import org.nutriGuideBuddy.features.meal.service.MealService;
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

  private final MealService service;
  private final MealAccessValidator mealAccessValidator;
  private final UserDetailsAccessValidator userDetailsAccessValidator;
  private final UserAccessValidator userAccessValidator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<MealView> getAll(@RequestBody(required = false) @Valid MealFilter filter) {
    return userDetailsAccessValidator.validateFullyRegistered().thenMany(service.getAll(filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> count(@RequestBody(required = false) @Valid MealFilter filter) {
    return userDetailsAccessValidator.validateFullyRegistered().then(service.count(filter));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MealView> create(@RequestBody @Valid MealCreateRequest dto) {
    return service.create(dto);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
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

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
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

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
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
}

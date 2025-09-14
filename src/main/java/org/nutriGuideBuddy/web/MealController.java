package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.MealCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealUpdateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealView;
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
  private final MealAccessValidator mealAccessValidator;
  private final UserDetailsAccessValidator userDetailsAccessValidator;
  private final UserAccessValidator userAccessValidator;

  @GetMapping
  public Flux<MealView> getAll() {
    return userDetailsAccessValidator.validateFullyRegistered().thenMany(service.getAll());
  }

  @GetMapping("/{id}")
  public Mono<MealView> getById(@PathVariable Long id) {
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
  public Mono<MealView> updateById(
      @RequestBody @Valid MealUpdateRequest dto, @PathVariable Long id) {
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
  public Mono<Void> deleteById(@PathVariable Long id) {
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

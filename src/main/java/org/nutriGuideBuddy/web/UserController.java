package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;
  private final UserAccessValidator accessValidator;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<UserView> create(
      @RequestBody @Valid UserCreateRequest userDto, @RequestParam String token) {
    return service.create(userDto, token);
  }

  @PostMapping("/get/all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<UserView> getAll(@RequestBody @Valid UserFilter filter) {
    return service.getAll(filter);
  }

  @PostMapping("/get/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> count(@RequestBody @Valid UserFilter filter) {
    return service.countByFilter(filter);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserView> getById(@PathVariable Long id) {
    return accessValidator
        .hasRole(UserRole.ADMIN)
        .flatMap(isAdmin -> isAdmin ? Mono.empty() : accessValidator.validateAccess(id))
        .then(service.getById(id));
  }

  @GetMapping("/{id}/with-details")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserWithDetailsView> getByIdWithDetails(@PathVariable Long id) {
    return accessValidator
        .hasRole(UserRole.ADMIN)
        .flatMap(isAdmin -> isAdmin ? Mono.empty() : accessValidator.validateAccess(id))
        .then(service.getByIdWithDetails(id));
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserView> me() {
    return service.me();
  }

  @GetMapping("/me/with-details")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserWithDetailsView> meWithDetails() {
    return service.meWithDetails();
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserView> update(
      @RequestBody @Valid UserUpdateRequest userDto, @PathVariable Long id) {
    return accessValidator
        .hasRole(UserRole.ADMIN)
        .flatMap(isAdmin -> isAdmin ? Mono.empty() : accessValidator.validateAccess(id))
        .then(service.update(userDto, id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable Long id) {
    return accessValidator
        .hasRole(UserRole.ADMIN)
        .flatMap(isAdmin -> isAdmin ? Mono.empty() : accessValidator.validateAccess(id))
        .then(service.delete(id));
  }
}

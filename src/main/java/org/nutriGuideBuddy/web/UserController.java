package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.*;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<UserView> create(
      @RequestBody @Valid UserCreateRequest userDto, @RequestParam String token) {
    return service.create(userDto, token);
  }

  @PostMapping("/get/all")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public Flux<UserView> getAll(@RequestBody @Valid UserFilter filter) {
    return service.getAll(filter);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') || @userAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserView> getById(@PathVariable String id) {
    return service.getById(id);
  }

  @GetMapping("/{id}/with-details")
  @PreAuthorize("hasRole('ADMIN') || @userAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserWithDetailsView> getByIdWithDetails(@PathVariable String id) {
    return service.getByIdWithDetails(id);
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
  @PreAuthorize("hasRole('ADMIN') || @userAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserView> update(
      @RequestBody @Valid UserUpdateRequest userDto, @PathVariable String id) {
    return service.update(userDto, id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') || @userAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable String id) {
    return service.delete(id);
  }
}

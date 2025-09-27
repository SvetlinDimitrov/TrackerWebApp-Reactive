package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.service.UserDetailsService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user-details")
@RequiredArgsConstructor
public class UserDetailsController {

  private final UserDetailsService service;
  private final UserDetailsAccessValidator validator;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> getById(@PathVariable Long id) {
    return validator.validateAccess(id).then(service.getById(id));
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> update(
      @RequestBody @Valid UserDetailsRequest userDto, @PathVariable Long id) {
    return validator.validateAccess(id).then(service.update(userDto, id));
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> me() {
    return service.me();
  }

  @PatchMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> updateMyDetails(@RequestBody @Valid UserDetailsRequest userDto) {
    return service.update(userDto);
  }
}

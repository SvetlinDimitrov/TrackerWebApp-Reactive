package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user_details.service.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user-details")
@RequiredArgsConstructor
public class UserDetailsController {

  private final UserDetailsService service;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') || @userDetailsAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> getById(@PathVariable String id) {
    return service.getById(id);
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> me() {
    return service.me();
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') || @userDetailsAccessValidator.hasAccess(#id)")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> update(
      @RequestBody @Valid UserDetailsRequest userDto, @PathVariable String id) {
    return service.update(userDto, id);
  }

  @PatchMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsView> updateMyDetails(@RequestBody @Valid UserDetailsRequest userDto) {
    return service.update(userDto);
  }
}

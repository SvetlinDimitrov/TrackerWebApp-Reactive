package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotView;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsAccessValidator;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserDetailsSnapshotAccessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user-details-snapshot")
@RequiredArgsConstructor
public class UserDetailsSnapshotController {

  private final UserDetailsSnapshotService service;
  private final UserDetailsSnapshotAccessValidator snapshotAccessValidator;
  private final UserDetailsAccessValidator userDetailsAccessValidator;
  private final UserAccessValidator userAccessValidator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<UserDetailsSnapshotProjection> getAll(
      @RequestBody(required = false) @Valid UserDetailsSnapshotFilter filter) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .thenMany(service.findAllByFilter(filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> countAll(
      @RequestBody(required = false) @Valid UserDetailsSnapshotFilter filter) {
    return userDetailsAccessValidator.validateFullyRegistered().then(service.countByFilter(filter));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsSnapshotView> get(@PathVariable Long id) {
    return userDetailsAccessValidator
        .validateFullyRegistered()
        .then(
            userAccessValidator
                .hasRole(UserRole.ADMIN)
                .flatMap(
                    isAdmin -> isAdmin ? Mono.empty() : snapshotAccessValidator.validateAccess(id)))
        .then(service.get(id));
  }
}

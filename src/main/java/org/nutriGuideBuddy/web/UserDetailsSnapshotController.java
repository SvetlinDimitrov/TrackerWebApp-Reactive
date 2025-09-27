package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotView;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
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
  private final UserDetailsSnapshotAccessValidator validator;

  @PostMapping("/get-all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<UserDetailsSnapshotProjection> getAll(
      @RequestBody(required = false) @Valid UserDetailsSnapshotFilter filter) {
    return validator.validateFullyRegistered().thenMany(service.findAllByFilter(filter));
  }

  @PostMapping("/get-all/count")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Long> countAll(
      @RequestBody(required = false) @Valid UserDetailsSnapshotFilter filter) {
    return validator.validateFullyRegistered().then(service.countByFilter(filter));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<UserDetailsSnapshotView> get(@PathVariable Long id) {
    return validator
        .validateFullyRegistered()
        .then(validator.validateAccess(id))
        .then(service.get(id));
  }
}

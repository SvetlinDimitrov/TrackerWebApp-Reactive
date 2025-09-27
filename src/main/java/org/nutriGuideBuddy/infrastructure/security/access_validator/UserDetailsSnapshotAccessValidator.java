package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsSnapshotAccessValidator extends AbstractAccessValidator {

  private final UserDetailsSnapshotService service;

  /** Owner-only: snapshot must belong to the authenticated user. */
  public Mono<Void> validateAccess(Long snapshotId) {
    return currentPrincipal()
        .map(p -> p.user().getId())
        .flatMap(userId -> service.existsByIdAndUserId(snapshotId, userId))
        .flatMap(
            has -> has ? Mono.empty() : Mono.error(new AccessDeniedException("Access denied")));
  }
}

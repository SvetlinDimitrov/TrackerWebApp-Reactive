package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserDetailsAccessValidator extends AbstractAccessValidator {

  /** Owner-only access: principal's UserDetails id must match the target id. */
  public Mono<Void> validateAccess(Long userDetailsId) {
    return currentPrincipal()
        .flatMap(
            p ->
                (p.details() != null && p.details().getId().equals(userDetailsId))
                    ? Mono.empty()
                    : Mono.error(new AccessDeniedException("Access denied")));
  }
}

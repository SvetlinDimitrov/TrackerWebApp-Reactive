package org.nutriGuideBuddy.infrastructure.security.access_validator;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAccessValidator extends AbstractAccessValidator {

  private final UserService userService;

  /** Owner-only: the authenticated user's id must match the target id. */
  public Mono<Void> validateAccess(Long ownerId) {
    return currentPrincipal()
        .flatMap(
            p ->
                p.user().getId().equals(ownerId)
                    ? Mono.empty()
                    : Mono.error(new AccessDeniedException("Access denied")));
  }

  /** Guard: completes if the current user has the role; otherwise 403. */
  public Mono<Void> hasRole(UserRole role) {
    return currentPrincipal()
        .flatMap(
            p ->
                p.user().getRole() == role
                    ? Mono.empty()
                    : Mono.error(new AccessDeniedException("Access denied")));
  }

  /** Guard: allow if user has the bypassRole OR is the owner. */
  public Mono<Void> ensureRoleOrOwner(UserRole bypassRole, Long ownerId) {
    return currentPrincipal()
        .flatMap(
            p -> {
              boolean hasRole = p.user().getRole() == bypassRole;
              boolean isOwner = p.user().getId().equals(ownerId);
              return (hasRole || isOwner)
                  ? Mono.empty()
                  : Mono.error(new AccessDeniedException("Access denied"));
            });
  }

  public Mono<Void> ensureNotGod(Long targetUserId) {
    return userService
        .findByIOrThrow(targetUserId)
        .flatMap(
            user ->
                user.getRole() == UserRole.GOD
                    ? Mono.error(
                        new AccessDeniedException("GOD user cannot be modified or deleted."))
                    : Mono.empty());
  }
}

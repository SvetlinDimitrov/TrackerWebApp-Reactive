package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserAccessValidator {

  public Mono<Void> validateAccess(Long id) {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              if (principal == null || !principal.user().getId().equals(id)) {
                return Mono.error(new AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }

  public Mono<Boolean> hasRole(UserRole role) {
    return ReactiveSecurityContextHolder.getContext()
        .map(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              return principal != null && role == principal.user().getRole();
            });
  }
}

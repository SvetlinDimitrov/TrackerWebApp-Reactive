package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserDetailsAccessValidator {

  public Mono<Void> validateAccess(Long id) {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              if (principal == null || !principal.details().getId().equals(id)) {
                return Mono.error(new AccessDeniedException("Access denied"));
              }
              return Mono.empty();
            });
  }

  public Mono<Void> validateFullyRegistered() {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              boolean fullyRegistered =
                  principal.details().getAge() != null
                      && principal.details().getHeight() != null
                      && principal.details().getKilograms() != null
                      && principal.details().getGender() != null
                      && principal.details().getWorkoutState() != null
                      && principal.details().getGoal() != null
                      && principal.details().getDiet() != null;
              if (!fullyRegistered) {
                return Mono.error(new AccessDeniedException("User is not fully registered"));
              }
              return Mono.empty();
            });
  }
}

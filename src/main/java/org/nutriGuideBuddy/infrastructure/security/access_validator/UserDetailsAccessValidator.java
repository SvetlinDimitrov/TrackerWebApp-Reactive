package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserDetailsAccessValidator {

  public Mono<Boolean> hasAccess(String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              return principal != null && principal.details().getId().equals(id);
            });
  }

  public Mono<Boolean> isFullyRegistered() {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();

              return Mono.just(principal.details())
                  .map(
                      userDetails ->
                          userDetails.getAge() != null
                              && userDetails.getHeight() != null
                              && userDetails.getKilograms() != null
                              && userDetails.getGender() != null
                              && userDetails.getWorkoutState() != null);
            });
  }
}

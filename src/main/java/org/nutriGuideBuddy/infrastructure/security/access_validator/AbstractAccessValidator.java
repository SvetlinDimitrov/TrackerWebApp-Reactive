package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

public abstract class AbstractAccessValidator {

  protected Mono<UserPrincipal> currentPrincipal() {
    return ReactiveUserDetailsServiceImpl.getPrincipal();
  }

  protected Mono<Long> currentUserId() {
    return ReactiveUserDetailsServiceImpl.getPrincipalId();
  }

  /** Widely used: ensure the current user is fully registered. */
  public Mono<Void> validateFullyRegistered() {
    return currentPrincipal()
        .flatMap(
            p -> {
              var d = p.details();
              boolean fullyRegistered =
                  d.getAge() != null
                      && d.getHeight() != null
                      && d.getKilograms() != null
                      && d.getGender() != null
                      && d.getWorkoutState() != null
                      && d.getGoal() != null
                      && d.getDiet() != null
                      && d.getNutritionAuthority() != null;
              return fullyRegistered
                  ? Mono.empty()
                  : Mono.error(new AccessDeniedException("User is not fully registered"));
            });
  }
}

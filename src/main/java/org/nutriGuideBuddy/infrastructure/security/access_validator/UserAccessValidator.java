package org.nutriGuideBuddy.infrastructure.security.access_validator;

import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserAccessValidator {

  public Mono<Boolean> hasAccess(String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(
            securityContext -> {
              UserPrincipal principal =
                  (UserPrincipal) securityContext.getAuthentication().getPrincipal();
              return principal != null && principal.user().getId().equals(id);
            });
  }
}

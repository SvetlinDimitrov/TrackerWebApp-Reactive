package org.nutriGuideBuddy.config.security.access_validator;

import org.nutriGuideBuddy.config.security.UserPrincipal;
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
}

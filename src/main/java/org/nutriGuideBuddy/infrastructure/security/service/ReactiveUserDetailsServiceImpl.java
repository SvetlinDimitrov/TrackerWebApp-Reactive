package org.nutriGuideBuddy.infrastructure.security.service;

import org.nutriGuideBuddy.features.user.service.UserDetailsService;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

  public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: %s";
  public static final String PRINCIPAL_NOT_FOUND =
      "Authenticated user not found in the security context.";
  private UserService userService;
  private UserDetailsService userDetailsService;

  @Override
  public Mono<UserDetails> findByUsername(String email) {
    return userService
        .findByEmail(email)
        .switchIfEmpty(
            Mono.error(new AccessDeniedException(String.format(USER_NOT_FOUND_BY_EMAIL, email))))
        .flatMap(
            user ->
                userDetailsService
                    .findByUserIdOrThrow(user.getId())
                    .map(details -> new UserPrincipal(user, details)));
  }

  public static Mono<UserPrincipal> getPrincipal() {
    return ReactiveSecurityContextHolder.getContext()
        .flatMap(
            context -> {
              Authentication authentication = context.getAuthentication();
              if (authentication instanceof UsernamePasswordAuthenticationToken token
                  && token.getPrincipal() instanceof UserPrincipal principal) {
                return Mono.just(principal);
              }
              return Mono.error(NotFoundException.message(PRINCIPAL_NOT_FOUND));
            });
  }

  public static Mono<Long> getPrincipalId() {
    return getPrincipal().map(userPrincipal -> userPrincipal.user().getId());
  }

  @Autowired
  public void setUserService(@Lazy UserService userService) {
    this.userService = userService;
  }

  @Autowired
  public void setUserDetailsService(@Lazy UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
}

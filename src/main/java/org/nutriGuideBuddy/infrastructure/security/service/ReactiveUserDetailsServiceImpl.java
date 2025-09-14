package org.nutriGuideBuddy.infrastructure.security.service;

import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.features.user_details.service.UserDetailsService;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

  private UserService userService;
  private UserDetailsService userDetailsService;

  @Override
  public Mono<UserDetails> findByUsername(String email) {
    return userService
        .findByEmailOrThrow(email)
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
              return Mono.error(new NotFoundException(ExceptionMessages.PRINCIPAL_NOT_FOUND));
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

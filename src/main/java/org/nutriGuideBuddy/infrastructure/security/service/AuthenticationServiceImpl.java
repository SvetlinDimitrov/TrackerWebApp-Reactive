package org.nutriGuideBuddy.infrastructure.security.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.INVALID_CREDENTIALS;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.mappers.AuthenticationMapper;
import org.nutriGuideBuddy.infrastructure.security.config.UserPrincipal;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationRequest;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final ReactiveUserDetailsServiceImpl reactiveUserDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService tokenService;
  private final AuthenticationMapper mapper;

  @Override
  public Mono<AuthenticationResponse> authenticate(AuthenticationRequest dto) {
    return reactiveUserDetailsService
        .findByUsername(dto.email())
        .cast(UserPrincipal.class)
        .flatMap(
            userPrincipal -> {
              if (!passwordEncoder.matches(dto.password(), userPrincipal.getPassword())) {
                return Mono.error(new BadRequestException(INVALID_CREDENTIALS));
              }
              JwtToken token = tokenService.generateToken(userPrincipal.getUsername());
              return Mono.just(mapper.toDto(userPrincipal.user(), userPrincipal.details(), token));
            });
  }
}

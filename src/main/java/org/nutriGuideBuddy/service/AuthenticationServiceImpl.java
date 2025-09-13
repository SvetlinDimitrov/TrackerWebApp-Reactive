package org.nutriGuideBuddy.service;

import static org.nutriGuideBuddy.exceptions.ExceptionMessages.INVALID_CREDENTIALS;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.config.security.UserPrincipal;
import org.nutriGuideBuddy.config.security.service.JwtTokenService;
import org.nutriGuideBuddy.config.security.service.ReactiveUserDetailsServiceImpl;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationRequest;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationResponse;
import org.nutriGuideBuddy.domain.dto.auth.JwtToken;
import org.nutriGuideBuddy.exceptions.BadRequestException;
import org.nutriGuideBuddy.mapper.AuthenticationMapper;
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

package org.nutriGuideBuddy.infrastructure.security.service;

import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;
import reactor.core.publisher.Mono;

public interface JwtTokenService {

  JwtToken generateToken(String email);

  Mono<String> validateToken(String token);
}

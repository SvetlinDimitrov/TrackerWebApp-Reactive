package org.nutriGuideBuddy.config.security.service;

import org.nutriGuideBuddy.domain.dto.auth.JwtToken;
import reactor.core.publisher.Mono;

public interface JwtTokenService {

  JwtToken generateToken(String email);

  Mono<String> validateToken(String token);
}

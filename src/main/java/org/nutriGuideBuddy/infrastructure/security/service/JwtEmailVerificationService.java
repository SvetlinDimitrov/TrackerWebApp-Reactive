package org.nutriGuideBuddy.infrastructure.security.service;

import reactor.core.publisher.Mono;

public interface JwtEmailVerificationService {
  String generateToken(String subject);

  Mono<String> validateToken(String token);
}

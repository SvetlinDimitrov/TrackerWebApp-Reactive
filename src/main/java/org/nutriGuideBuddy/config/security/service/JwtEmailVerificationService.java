package org.nutriGuideBuddy.config.security.service;

import reactor.core.publisher.Mono;

public interface JwtEmailVerificationService {
  String generateToken(String subject);

  Mono<String> validateToken(String token);
}

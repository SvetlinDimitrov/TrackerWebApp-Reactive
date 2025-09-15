package org.nutriGuideBuddy.infrastructure.security.service;

import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationRequest;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

  Mono<AuthenticationResponse> authenticate(AuthenticationRequest dto);
}

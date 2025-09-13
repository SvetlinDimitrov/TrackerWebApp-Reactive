package org.nutriGuideBuddy.service;

import org.nutriGuideBuddy.domain.dto.auth.AuthenticationResponse;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationRequest;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

  Mono<AuthenticationResponse> authenticate(AuthenticationRequest dto);
}

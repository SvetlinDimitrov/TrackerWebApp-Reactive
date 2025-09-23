package org.nutriGuideBuddy.infrastructure.email;

import org.nutriGuideBuddy.infrastructure.security.dto.EmailValidationRequest;
import reactor.core.publisher.Mono;

public interface EmailVerificationService {

  Mono<Void> validateUserAndSendVerificationEmail(EmailValidationRequest dto);

  Mono<Void> sendForgotPasswordEmail(String email);
}

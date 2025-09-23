package org.nutriGuideBuddy.infrastructure.security.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.infrastructure.email.EmailVerificationService;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationRequest;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.infrastructure.security.dto.EmailValidationRequest;
import org.nutriGuideBuddy.infrastructure.security.dto.ResetPasswordRequest;
import org.nutriGuideBuddy.infrastructure.security.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final EmailVerificationService emailVerificationService;
  private final AuthenticationService authenticationService;
  private final UserService userService;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<AuthenticationResponse> loginUser(@RequestBody AuthenticationRequest dto) {
    return authenticationService.authenticate(dto);
  }

  @PostMapping("/request-verification-email")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> sendVerificationEmail(@Valid @RequestBody EmailValidationRequest userDto) {
    return emailVerificationService.validateUserAndSendVerificationEmail(userDto);
  }

  @PostMapping("/reset-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> sendForgotPasswordEmail(@Valid @RequestBody ResetPasswordRequest request) {
    return emailVerificationService.sendForgotPasswordEmail(request.email());
  }

  @PatchMapping("/change-password")
  public Mono<Void> modifyPassword(
      @RequestBody @Valid ChangePasswordRequest dto, @RequestParam String token) {
    return userService.modifyPassword(dto, token);
  }
}

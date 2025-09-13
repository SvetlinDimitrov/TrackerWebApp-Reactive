package org.nutriGuideBuddy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.domain.dto.auth.ResetPasswordRequest;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationRequest;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationResponse;
import org.nutriGuideBuddy.domain.dto.auth.ChangePasswordRequest;
import org.nutriGuideBuddy.domain.dto.auth.EmailValidationRequest;
import org.nutriGuideBuddy.service.AuthenticationService;
import org.nutriGuideBuddy.service.EmailVerificationService;
import org.nutriGuideBuddy.service.UserService;
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
      @RequestBody ChangePasswordRequest dto, @RequestParam String token) {
    return userService.modifyPassword(dto, token);
  }
}

package org.nutriGuideBuddy.infrastructure.brevo_api;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.infrastructure.security.service.JwtEmailVerificationService;
import org.nutriGuideBuddy.infrastructure.security.dto.EmailValidationRequest;
import org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

  @Value("${sendinblue.api.key}")
  private String sendinblueApiKey;

  @Value("${front-end.url}")
  private String frontendUrl;

  @Value("${api.email.sender}")
  private String emailSender;

  private final UserRepository userRepository;
  private final JwtEmailVerificationService jwtUtil;

  public Mono<Void> validateUserAndSendVerificationEmail(EmailValidationRequest dto) {
    return userRepository
        .existsByEmail(dto.email())
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.error(new ValidationException(Map.of("email", "User already exists")));
              } else {
                ApiClient defaultClient = Configuration.getDefaultApiClient();
                ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
                apiKey.setApiKey(sendinblueApiKey);

                TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
                String verificationUrl =
                    frontendUrl + "/email-verification?token=" + jwtUtil.generateToken(dto.email());
                SendSmtpEmail email =
                    getSendSmtpEmail(
                        dto.email(),
                        verificationUrl,
                        "Email Verification",
                        "Please verify your email by clicking the link below: ");
                try {
                  apiInstance.sendTransacEmail(email);
                  return Mono.empty();
                } catch (ApiException e) {
                  return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS));
                }
              }
            });
  }

  public Mono<Void> sendForgotPasswordEmail(String email) {
    return userRepository
        .existsByEmail(email)
        .flatMap(
            exists -> {
              if (!exists) {
                return Mono.error(new NotFoundException(ExceptionMessages.USER_NOT_FOUND));
              }
              ApiClient defaultClient = Configuration.getDefaultApiClient();
              ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
              apiKey.setApiKey(sendinblueApiKey);

              TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
              String verificationUrl =
                  frontendUrl + "/recreate-password?token=" + jwtUtil.generateToken(email);
              SendSmtpEmail emailSend =
                  getSendSmtpEmail(
                      email,
                      verificationUrl,
                      "Reset Password",
                      "Please reset your password by clicking the link below: ");

              try {
                apiInstance.sendTransacEmail(emailSend);
                return Mono.empty();
              } catch (ApiException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS));
              }
            });
  }

  private SendSmtpEmail getSendSmtpEmail(
      String recipientEmail, String verificationUrl, String subject, String text) {
    SendSmtpEmailSender sender = new SendSmtpEmailSender();
    sender.setEmail(emailSender);
    sender.setName("Dont replay");

    SendSmtpEmailTo to = new SendSmtpEmailTo();
    to.setEmail(recipientEmail);

    SendSmtpEmail email = new SendSmtpEmail();
    email.setSender(sender);
    email.setTo(List.of(to));
    email.setSubject(subject);
    email.setHtmlContent(
        "<html><body><p>"
            + text
            + "<a href=\""
            + verificationUrl
            + "\">Verify Email</a></p></body></html>");
    return email;
  }
}

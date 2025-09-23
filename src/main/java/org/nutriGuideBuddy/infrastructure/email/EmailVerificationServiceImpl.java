package org.nutriGuideBuddy.infrastructure.mail;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.USER_NOT_FOUND_BY_EMAIL;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.service.UserService;
import org.nutriGuideBuddy.infrastructure.email.EmailVerificationService;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ValidationException;
import org.nutriGuideBuddy.infrastructure.security.dto.EmailValidationRequest;
import org.nutriGuideBuddy.infrastructure.security.service.JwtEmailVerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

  @Value("${front-end.url}")
  private String frontendUrl;

  @Value("${mail.from.email}")
  private String fromEmail;

  @Value("${mail.from.name}")
  private String fromName;

  private final JavaMailSender mailSender;
  private final UserService userService;
  private final JwtEmailVerificationService jwtUtil;
  private final SpringTemplateEngine emailTemplateEngine;

  public Mono<Void> validateUserAndSendVerificationEmail(EmailValidationRequest dto) {
    return userService
        .existsByEmail(dto.email())
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.error(new ValidationException(Map.of("email", "User already exists")));
              }
              String token = jwtUtil.generateToken(dto.email());
              String url = frontendUrl + "/email-verification?token=" + token;
              return sendTemplateEmail(
                  dto.email(),
                  "Email Verification",
                  "verify-email", // thymeleaf template name (verify-email.html)
                  Map.of(
                      "title", "Verify your email",
                      "buttonText", "Verify Email",
                      "intro", "Please verify your email by clicking the button below:",
                      "actionUrl", url));
            });
  }

  public Mono<Void> sendForgotPasswordEmail(String email) {
    return userService
        .existsByEmail(email)
        .flatMap(
            exists -> {
              if (!exists) {
                return Mono.error(
                    new NotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
              }
              String token = jwtUtil.generateToken(email);
              String url = frontendUrl + "/recreate-password?token=" + token;
              return sendTemplateEmail(
                  email,
                  "Reset Password",
                  "reset-password", // thymeleaf template name (reset-password.html)
                  Map.of(
                      "title", "Reset your password",
                      "buttonText", "Reset Password",
                      "intro", "Click the button below to reset your password:",
                      "actionUrl", url));
            });
  }

  private Mono<Void> sendTemplateEmail(
      String recipientEmail, String subject, String templateName, Map<String, Object> model) {

    return Mono.fromCallable(
            () -> {
              // render html
              Context ctx = new Context(); // optionally pass Locale here
              ctx.setVariables(model);
              String html = emailTemplateEngine.process(templateName, ctx);

              // compose message
              MimeMessage message = mailSender.createMimeMessage();
              MimeMessageHelper helper =
                  new MimeMessageHelper(
                      message,
                      MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                      StandardCharsets.UTF_8.name());
              helper.setFrom(fromEmail, fromName);
              helper.setTo(recipientEmail);
              helper.setSubject(subject);
              helper.setText(html, true);

              // send
              mailSender.send(message);
              return true;
            })
        .doOnSuccess(v -> log.info("Email '{}' sent to {}", subject, recipientEmail))
        .onErrorMap(
            MessagingException.class,
            e -> {
              log.error("MessagingException while sending email", e);
              return new ResponseStatusException(
                  HttpStatus.INTERNAL_SERVER_ERROR, "Unable to compose email");
            })
        .onErrorMap(
            MailException.class,
            e -> {
              log.error("MailException while sending email", e);
              return new ResponseStatusException(
                  HttpStatus.SERVICE_UNAVAILABLE, "Email temporarily unavailable");
            })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
  }
}

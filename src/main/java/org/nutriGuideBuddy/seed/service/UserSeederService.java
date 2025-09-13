package org.nutriGuideBuddy.seed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserSeederService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private static final String DEFAULT_PASSWORD = "12345";

  public Mono<Void> seed() {
    log.info("Starting user seeding...");
    return Flux.fromArray(EmailEnum.values())
        .flatMap(
            emailEnum ->
                userRepository
                    .existsByEmail(emailEnum.getEmail())
                    .filter(exists -> !exists)
                    .flatMap(
                        exists -> {
                          var user = new User();
                          user.setUsername(emailEnum.name().toLowerCase());
                          user.setEmail(emailEnum.getEmail());
                          user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                          user.setRole(UserRole.USER);
                          log.info("Seeding user: {}", user.getEmail());
                          return userRepository
                              .save(user)
                              .doOnSuccess(u -> log.debug("User seeded: {}", u));
                        }))
        .then() // Return Mono<Void> after seeding is complete
        .doOnTerminate(() -> log.info("User seeding completed."));
  }
}

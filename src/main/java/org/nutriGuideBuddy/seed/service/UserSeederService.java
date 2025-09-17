package org.nutriGuideBuddy.seed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSeederService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private static final String DEFAULT_PASSWORD = "12345";

  public Mono<Void> seed() {
    log.info("Starting User seeding...");

    return Flux.fromArray(EmailEnum.values())
        .flatMap(
            emailEnum ->
                userRepository
                    .existsByEmail(emailEnum.getEmail())
                    .flatMap(
                        exists -> {
                          if (exists) return Mono.empty();

                          User user = new User();
                          user.setUsername(emailEnum.name().toLowerCase());
                          user.setEmail(emailEnum.getEmail());
                          user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                          user.setRole(UserRole.USER);

                          return userRepository
                              .save(user)
                              .doOnSuccess(
                                  u ->
                                      log.info(
                                          "🧑 Seeded user '{}' (email '{}')",
                                          u.getUsername(),
                                          u.getEmail()));
                        }))
        .then(Mono.fromRunnable(() -> log.info("User seeding completed.")));
  }
}

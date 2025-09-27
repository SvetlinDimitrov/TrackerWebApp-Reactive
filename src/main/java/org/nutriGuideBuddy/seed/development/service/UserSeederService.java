package org.nutriGuideBuddy.seed.development.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.development.enums.UsersForSeed;
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

    return Flux.fromArray(UsersForSeed.values())
        .flatMap(
            userDef ->
                userRepository
                    .existsByEmail(userDef.getEmail())
                    .flatMap(
                        exists -> {
                          if (exists) return Mono.empty();

                          User user = new User();
                          user.setUsername(userDef.name().toLowerCase());
                          user.setEmail(userDef.getEmail());
                          user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                          user.setRole(userDef.getRole());

                          return userRepository
                              .save(user)
                              .doOnSuccess(
                                  u -> {
                                    if (userDef == UsersForSeed.GOD) {
                                      log.info(
                                          "👑 Seeded GOD user '{}' (email '{}')",
                                          u.getUsername(),
                                          u.getEmail());
                                    } else {
                                      log.info(
                                          "🧑 Seeded user '{}' (email '{}')",
                                          u.getUsername(),
                                          u.getEmail());
                                    }
                                  });
                        }))
        .then(Mono.fromRunnable(() -> log.info("User seeding completed.")));
  }
}

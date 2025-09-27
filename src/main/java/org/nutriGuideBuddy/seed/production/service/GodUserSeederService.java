package org.nutriGuideBuddy.seed.production.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.nutriGuideBuddy.features.user.repository.UserDetailsRepository;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GodUserSeederService {

  private final UserRepository userRepository;
  private final UserDetailsRepository userDetailsRepository;
  private final UserDetailsSnapshotService userDetailsSnapshotService;
  private final PasswordEncoder passwordEncoder;

  @Value("${god.user.email}")
  private String godEmail;

  @Value("${god.user.password}")
  private String godPassword;

  /**
   * Ensures there is exactly ONE GOD user (by email) in production. If missing: creates it, then
   * ensures UserDetails + initial snapshot. If present: only ensures UserDetails + initial
   * snapshot.
   */
  public Mono<Void> seed() {
    return userRepository
        .deleteAllByRole(UserRole.GOD)
        .then(createGodUser(godEmail, godPassword))
        .flatMap(this::ensureUserDetailsAndSnapshot)
        .then();
  }

  private Mono<User> createGodUser(String email, String rawPassword) {
    User user = new User();
    user.setUsername("god");
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(UserRole.GOD);
    return userRepository
        .save(user)
        .doOnSuccess(
            u -> log.info("👑 Seeded GOD user '{}' (email '{}')", u.getUsername(), u.getEmail()));
  }

  private Mono<User> ensureUserDetailsAndSnapshot(User user) {
    return userDetailsRepository
        .existsByUserId(user.getId())
        .flatMap(
            exists -> {
              if (Boolean.TRUE.equals(exists)) {
                return Mono.just(user);
              }
              UserDetails details = new UserDetails();
              details.setUserId(user.getId());
              details.setKilograms(80.0);
              details.setHeight(180.0);
              details.setAge(30);

              details.setDiet(DietType.NONE);
              details.setGoal(Goals.MAINTAIN_WEIGHT);
              details.setWorkoutState(WorkoutState.LIGHTLY_ACTIVE);
              details.setGender(Gender.MALE);
              details.setNutritionAuthority(NutritionAuthority.WHO_FAO);

              return userDetailsRepository
                  .save(details)
                  .flatMap(saved -> userDetailsSnapshotService.create(saved).thenReturn(user))
                  .doOnSuccess(
                      u ->
                          log.info(
                              "📸 Created initial snapshot & seeded details for GOD user '{}'",
                              u.getEmail()));
            });
  }
}

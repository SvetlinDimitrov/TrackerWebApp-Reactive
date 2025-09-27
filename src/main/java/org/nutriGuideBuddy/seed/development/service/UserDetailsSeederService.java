package org.nutriGuideBuddy.seed.development.service;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.nutriGuideBuddy.features.user.repository.UserDetailsRepository;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.nutriGuideBuddy.seed.development.enums.UsersForSeed;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsSeederService {

  private final UserRepository userRepository;
  private final UserDetailsRepository userDetailsRepository;
  private final UserDetailsSnapshotService userDetailsSnapshotService;
  private final Random random = new Random();

  public Mono<Void> seed() {
    log.info("Starting UserDetails seeding...");

    // cleaner way to gather emails from enum
    Set<String> emails =
        Arrays.stream(UsersForSeed.values())
            .map(UsersForSeed::getEmail)
            .collect(Collectors.toSet());

    return userRepository
        .findAllByEmailIn(emails)
        .flatMap(
            user ->
                userDetailsRepository
                    .existsByUserId(user.getId())
                    .flatMap(
                        exists -> {
                          if (exists) return Mono.empty();

                          UserDetails d = new UserDetails();
                          d.setUserId(user.getId());

                          if (user.getRole() == UserRole.GOD) {
                            d.setKilograms(80.0);
                            d.setHeight(180.0);
                            d.setAge(30);
                            d.setDiet(DietType.NONE);
                            d.setGoal(Goals.MAINTAIN_WEIGHT);
                            d.setWorkoutState(WorkoutState.LIGHTLY_ACTIVE);
                            d.setGender(Gender.MALE);
                            d.setNutritionAuthority(NutritionAuthority.WHO_FAO);
                          } else {
                            // Randomized values for dev users
                            d.setKilograms(50.0 + random.nextDouble() * 70.0); // 50–120 kg
                            d.setHeight(150.0 + random.nextDouble() * 50.0); // 150–200 cm
                            d.setAge(random.nextInt(56) + 25); // 25–80

                            WorkoutState[] workoutStates = WorkoutState.values();
                            Gender[] genders = Gender.values();
                            Goals[] goals = Goals.values();
                            DietType[] dietTypes = DietType.values();
                            NutritionAuthority[] nutritionAuthorities = NutritionAuthority.values();

                            d.setDiet(dietTypes[random.nextInt(dietTypes.length)]);
                            d.setGoal(goals[random.nextInt(goals.length)]);
                            d.setWorkoutState(workoutStates[random.nextInt(workoutStates.length)]);
                            d.setGender(genders[random.nextInt(genders.length)]);
                            d.setNutritionAuthority(
                                nutritionAuthorities[random.nextInt(nutritionAuthorities.length)]);
                          }

                          return userDetailsRepository
                              .save(d)
                              .flatMap(
                                  saved ->
                                      userDetailsSnapshotService.create(saved).thenReturn(saved))
                              .doOnSuccess(
                                  saved ->
                                      log.info(
                                          "📸 Created initial snapshot & seeded details '{}' (user '{}')",
                                          saved,
                                          user.getEmail()))
                              .then();
                        }))
        .doOnComplete(() -> log.info("UserDetails seeding completed."))
        .then();
  }
}

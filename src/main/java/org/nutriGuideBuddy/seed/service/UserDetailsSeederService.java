package org.nutriGuideBuddy.seed.service;

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
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.nutriGuideBuddy.features.user.repository.UserDetailsRepository;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user.service.UserDetailsSnapshotService;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
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

    Set<String> emails =
        Set.of(EmailEnum.values()).stream().map(EmailEnum::getEmail).collect(Collectors.toSet());

    return userRepository
        .findAllByEmailIn(emails)
        .flatMap(
            user ->
                userDetailsRepository
                    .existsByUserId(user.getId())
                    .flatMap(
                        exists -> {
                          if (exists) {
                            return Mono.empty();
                          }

                          UserDetails userDetails = new UserDetails();
                          userDetails.setUserId(user.getId());
                          userDetails.setKilograms(50.0 + random.nextDouble() * 70.0);
                          userDetails.setHeight(150.0 + random.nextDouble() * 50.0);
                          userDetails.setAge(random.nextInt(56) + 25);

                          WorkoutState[] workoutStates = WorkoutState.values();
                          Gender[] genders = Gender.values();
                          Goals[] goals = Goals.values();
                          DietType[] dietTypes = DietType.values();
                          NutritionAuthority[] nutritionAuthorities = NutritionAuthority.values();

                          userDetails.setDiet(dietTypes[random.nextInt(dietTypes.length)]);
                          userDetails.setGoal(goals[random.nextInt(goals.length)]);
                          userDetails.setWorkoutState(
                              workoutStates[random.nextInt(workoutStates.length)]);
                          userDetails.setGender(genders[random.nextInt(genders.length)]);
                          userDetails.setNutritionAuthority(
                              nutritionAuthorities[random.nextInt(nutritionAuthorities.length)]);
                          return userDetailsRepository
                              .save(userDetails)
                              .flatMap(
                                  saved ->
                                      userDetailsSnapshotService
                                          .create(saved)
                                          .then(
                                              Mono.fromRunnable(
                                                  () ->
                                                      log.info(
                                                          "📸 Created initial snapshot & seeded details '{}' (user '{}')",
                                                          saved,
                                                          user.getEmail()))));
                        }))
        .doOnComplete(() -> log.info("UserDetails seeding completed."))
        .then();
  }
}

package org.nutriGuideBuddy.seed.service;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.nutriGuideBuddy.features.user.repository.UserDetailsRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsSeederService {

  private final UserRepository userRepository;
  private final UserDetailsRepository userDetailsRepository;
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
                    .filter(exists -> !exists) // Proceed only if they do not exist
                    .flatMap(
                        exists -> {
                          UserDetails userDetails = new UserDetails();
                          userDetails.setUserId(user.getId());
                          userDetails.setKilograms(50.0 + random.nextDouble() * 70.0);
                          userDetails.setHeight(150.0 + random.nextDouble() * 50.0);
                          userDetails.setAge(random.nextInt(56) + 25);

                          WorkoutState[] workoutStates = WorkoutState.values();
                          Gender[] genders = Gender.values();
                          userDetails.setWorkoutState(
                              workoutStates[random.nextInt(workoutStates.length)]);
                          userDetails.setGender(genders[random.nextInt(genders.length)]);

                          return userDetailsRepository
                              .save(userDetails)
                              .doOnSuccess(
                                  saved -> log.info("UserDetails seeded successfully: {}", saved));
                        }))
        .doOnComplete(() -> log.info("UserDetails seeding completed."))
        .then();
  }
}

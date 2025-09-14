package org.nutriGuideBuddy.seed.service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.nutriGuideBuddy.features.user_details.enums.Gender;
import org.nutriGuideBuddy.features.user_details.enums.WorkoutState;
import org.nutriGuideBuddy.features.user_details.repository.UserDetailsRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("dev")
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
                          userDetails.setKilograms(
                              BigDecimal.valueOf(50.0 + random.nextDouble() * 70.0));
                          userDetails.setHeight(
                              BigDecimal.valueOf(150.0 + random.nextDouble() * 50.0));
                          userDetails.setAge(random.nextInt(56) + 25);

                          WorkoutState[] workoutStates = WorkoutState.values();
                          Gender[] genders = Gender.values();
                          userDetails.setWorkoutState(
                              workoutStates[random.nextInt(workoutStates.length)]);
                          userDetails.setGender(genders[random.nextInt(genders.length)]);

                          log.debug("UserDetails created: {}", userDetails);
                          return userDetailsRepository.save(userDetails);
                        }))
        .doOnComplete(() -> log.info("UserDetails seeding completed."))
        .then();
  }
}

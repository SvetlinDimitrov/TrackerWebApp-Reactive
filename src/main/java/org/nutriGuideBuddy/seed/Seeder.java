package org.nutriGuideBuddy.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.seed.service.MealFoodSeederService;
import org.nutriGuideBuddy.seed.service.MealSeederService;
import org.nutriGuideBuddy.seed.service.UserDetailsSeederService;
import org.nutriGuideBuddy.seed.service.UserSeederService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

  private final UserSeederService userSeederService;
  private final UserDetailsSeederService userDetailsSeederService;
  private final MealSeederService mealSeederService;
  private final MealFoodSeederService mealFoodSeederService;

  @Override
  public void run(String... args) {
    log.info("Seeding data...");

    userSeederService
        .seed()
        .then(userDetailsSeederService.seed())
        .then(mealSeederService.seed())
        .then(mealFoodSeederService.seed())
        .doOnTerminate(() -> log.info("Data seeding completed."))
        .subscribe();
  }
}

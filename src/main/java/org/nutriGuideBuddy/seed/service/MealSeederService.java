package org.nutriGuideBuddy.seed.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealSeederService {

  private final MealRepository mealRepository;
  private final UserRepository userRepository;

  private static final Set<String> MEALS = Set.of("Breakfast", "Lunch", "Snack", "Dinner");

  public Mono<Void> seed() {
    log.info("Starting Meal seeding...");

    return userRepository
        .findAll()
        .flatMap(
            user ->
                Mono.when(
                    MEALS.stream()
                        .map(
                            mealName ->
                                mealRepository
                                    .existsByNameAndUserId(mealName, user.getId())
                                    .filter(exists -> !exists)
                                    .flatMap(
                                        exists -> {
                                          Meal meal = new Meal();
                                          meal.setUserId(user.getId());
                                          meal.setName(mealName);
                                          return mealRepository
                                              .save(meal)
                                              .doOnSuccess(
                                                  saved ->
                                                      log.info(
                                                          "Meal seeded successfully: {}", saved));
                                        }))
                        .toList()))
        .doOnComplete(() -> log.info("Meal seeding completed."))
        .then();
  }
}

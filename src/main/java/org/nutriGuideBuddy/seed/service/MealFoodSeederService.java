package org.nutriGuideBuddy.seed.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
import org.nutriGuideBuddy.seed.utils.FoodSeedUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealFoodSeederService {

  private final MealRepository mealRepository;
  private final MealFoodService mealFoodService;
  private final UserRepository userRepository;
  private final FoodSeedUtils foodSeedUtils;

  private static final List<String> FOOD_NAMES =
      List.of(
          "Apple",
          "Banana",
          "Chicken Breast",
          "Rice",
          "Salmon",
          "Bread",
          "Cheese",
          "Milk",
          "Almonds",
          "Eggs",
          "Broccoli",
          "Pasta",
          "Beef",
          "Yogurt",
          "Orange");

  private static final List<String> PICTURES =
      List.of(
          "https://picsum.photos/200", "https://picsum.photos/201", "https://picsum.photos/202");

  private final Random random = new Random();

  public Mono<Void> seed() {
    log.info("Starting MealFood seeding...");

    Set<String> emails =
        Arrays.stream(EmailEnum.values()).map(EmailEnum::getEmail).collect(Collectors.toSet());

    return userRepository
        .findAllByEmailIn(emails)
        .collectList()
        .flatMapMany(
            users ->
                mealRepository
                    .findAll()
                    .flatMap(
                        meal ->
                            mealFoodService
                                .countByMealIdAndFilter(meal.getId(), new MealFoodFilter())
                                .flatMapMany(
                                    count -> {
                                      if (count > 0) return Flux.empty();

                                      int foodCount =
                                          ThreadLocalRandom.current()
                                              .nextInt(5, 12); // 5–11 inclusive

                                      // ensure enough names, even if we need duplicates
                                      List<String> shuffledNames = new ArrayList<>(FOOD_NAMES);
                                      Collections.shuffle(shuffledNames, random);

                                      List<String> chosenNames = new ArrayList<>();
                                      for (int i = 0; i < foodCount; i++) {
                                        String baseName =
                                            shuffledNames.get(i % shuffledNames.size());
                                        String uniqueName =
                                            (i < shuffledNames.size())
                                                ? baseName
                                                : baseName + " " + (i / shuffledNames.size() + 1);
                                        chosenNames.add(uniqueName);
                                      }

                                      List<Mono<?>> creations = new ArrayList<>();
                                      for (int i = 0; i < chosenNames.size(); i++) {
                                        User owner = users.get(random.nextInt(users.size()));
                                        String foodName = chosenNames.get(i);

                                        FoodCreateRequest dto =
                                            new FoodCreateRequest(
                                                foodName,
                                                "Info about " + i,
                                                "Detailed info about " + i,
                                                randomPicture(),
                                                foodSeedUtils.randomCalorieAmount(),
                                                CalorieUnits.KCAL,
                                                foodSeedUtils.randomServings(),
                                                foodSeedUtils.randomNutritions());

                                        creations.add(
                                            mealFoodService
                                                .create(dto, meal.getId(), owner.getId())
                                                .doOnSuccess(
                                                    food ->
                                                        log.info(
                                                            "🥑 Seeded food '{}' for meal '{}' (user '{}')",
                                                            food,
                                                            meal.getName(),
                                                            owner.getEmail())));
                                      }

                                      return Flux.merge(creations);
                                    })))
        .then()
        .doOnTerminate(() -> log.info("MealFood seeding completed."));
  }

  private String randomPicture() {
    return PICTURES.get(random.nextInt(PICTURES.size()));
  }
}

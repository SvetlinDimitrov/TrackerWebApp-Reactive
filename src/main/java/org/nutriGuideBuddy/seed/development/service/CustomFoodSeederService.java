package org.nutriGuideBuddy.seed.development.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.custom_food.service.CustomFoodServiceImpl;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.development.enums.UsersForSeed;
import org.nutriGuideBuddy.seed.development.utils.FoodSeedUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomFoodSeederService {

  private final CustomFoodServiceImpl customFoodService;
  private final UserRepository userRepository;
  private final FoodSeedUtils foodSeedUtils;

  private static final List<String> FOOD_NAMES =
      List.of("Tofu", "Avocado", "Spinach", "Lentils", "Quinoa", "Peanuts", "Oats", "Shrimp");

  private static final List<String> PICTURES =
      List.of(
          "https://picsum.photos/210", "https://picsum.photos/211", "https://picsum.photos/212");

  private final Random random = new Random();

  public Mono<Void> seed() {
    log.info("Starting CustomFood seeding...");

    Set<String> emails = UsersForSeed.emailsExceptGod();

    return userRepository
        .findAllByEmailIn(emails)
        .collectList()
        .flatMapMany(
            users ->
                Flux.fromIterable(users)
                    .flatMap(
                        user ->
                            customFoodService
                                .countByUserId(user.getId())
                                .flatMapMany(
                                    count -> {
                                      if (count > 0) {
                                        return Flux.empty();
                                      }

                                      int foodCount = ThreadLocalRandom.current().nextInt(5, 11);

                                      List<String> shuffledNames = new ArrayList<>(FOOD_NAMES);
                                      Collections.shuffle(shuffledNames, random);

                                      List<String> chosenNames =
                                          getChosenNames(foodCount, shuffledNames);

                                      List<Mono<?>> creations = new ArrayList<>();
                                      for (int i = 0; i < chosenNames.size(); i++) {
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
                                            customFoodService
                                                .create(dto, user.getId())
                                                .doOnSuccess(
                                                    food ->
                                                        log.info(
                                                            "🥗 Seeded custom food '{}' (user '{}')",
                                                            food,
                                                            user.getEmail())));
                                      }

                                      return Flux.merge(creations);
                                    })))
        .then()
        .doOnTerminate(() -> log.info("CustomFood seeding completed."));
  }

  private List<String> getChosenNames(int foodCount, List<String> shuffledNames) {
    List<String> chosenNames = new ArrayList<>();
    for (int i = 0; i < foodCount; i++) {
      String baseName = shuffledNames.get(i % shuffledNames.size());
      String uniqueName =
          (i < shuffledNames.size()) ? baseName : baseName + " " + (i / shuffledNames.size() + 1);
      chosenNames.add(uniqueName);
    }
    return chosenNames;
  }

  private String randomPicture() {
    return PICTURES.get(random.nextInt(PICTURES.size()));
  }
}

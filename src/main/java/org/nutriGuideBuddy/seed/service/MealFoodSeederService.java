package org.nutriGuideBuddy.seed.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.repository.UserRepository;
import org.nutriGuideBuddy.seed.enums.EmailEnum;
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
    log.info("Starting Food seeding...");

    Set<String> emails =
        Set.of(EmailEnum.values()).stream().map(EmailEnum::getEmail).collect(Collectors.toSet());

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
                                      if (count > 0) {
                                        return Flux.empty();
                                      }

                                      int foodCount =
                                          ThreadLocalRandom.current().nextInt(5, 11); // 5–10 foods
                                      List<Mono<?>> creations = new ArrayList<>();

                                      for (int i = 0; i < foodCount; i++) {
                                        User owner =
                                            users.get(
                                                random.nextInt(
                                                    users.size())); // assign random seeded user

                                        FoodCreateRequest dto =
                                            new FoodCreateRequest(
                                                randomFoodName(),
                                                "Info about " + i,
                                                "Detailed info about " + i,
                                                randomPicture(),
                                                randomCalorieAmount(),
                                                CalorieUnits.KCAL,
                                                randomServings(),
                                                randomNutritions());

                                        creations.add(
                                            mealFoodService
                                                .create(dto, meal.getId(), owner.getId())
                                                .doOnSuccess(
                                                    food ->
                                                        log.info(
                                                            "\uD83E\uDD57 Seeded food '{}' for meal '{}' (user '{}')",
                                                            food,
                                                            meal.getName(),
                                                            owner.getEmail())));
                                      }
                                      return Flux.merge(creations);
                                    })))
        .then()
        .doOnTerminate(() -> log.info("Food seeding completed."));
  }

  private String randomFoodName() {
    return FOOD_NAMES.get(random.nextInt(FOOD_NAMES.size()));
  }

  private String randomPicture() {
    return PICTURES.get(random.nextInt(PICTURES.size()));
  }

  private Double randomCalorieAmount() {
    return 50.0 + random.nextDouble() * 500.0;
  }

  private Set<ServingCreateRequest> randomServings() {
    int count = ThreadLocalRandom.current().nextInt(2, 6);
    Set<ServingCreateRequest> servings = new HashSet<>();

    List<ServingMetric> metrics = Arrays.asList(ServingMetric.values());
    Collections.shuffle(metrics);

    for (int i = 0; i < count; i++) {
      ServingMetric randomMetric = ServingMetric.values()[random.nextInt(ServingMetric.values().length)];
      boolean isMain = (i == 0);
      servings.add(
          new ServingCreateRequest(
              10.0 + random.nextDouble() * 200.0, randomMetric, isMain));
    }
    return servings;
  }

  private Set<NutritionCreateRequest> randomNutritions() {
    int count = ThreadLocalRandom.current().nextInt(5, 21);
    List<AllowedNutrients> nutrients = Arrays.asList(AllowedNutrients.values());
    Collections.shuffle(nutrients);

    return nutrients.subList(0, count).stream()
        .map(
            n ->
                new NutritionCreateRequest(
                    n.getNutrientName(),
                    n.getNutrientUnit(),
                    randomNutritionAmount(n.getNutrientUnit())))
        .collect(Collectors.toSet());
  }

  private Double randomNutritionAmount(String unit) {
    return switch (unit) {
      case "g" -> 0.1 + random.nextDouble() * 50.0;
      case "mg" -> 1.0 + random.nextDouble() * 500.0;
      case "µg", "μg" -> 5.0 + random.nextDouble() * 1000.0;
      case "L" -> 0.1 + random.nextDouble() * 2.0;
      default -> 1.0;
    };
  }
}

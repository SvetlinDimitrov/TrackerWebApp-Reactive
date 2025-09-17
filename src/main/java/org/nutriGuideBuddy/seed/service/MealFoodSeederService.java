package org.nutriGuideBuddy.seed.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.meal.dto.MealFoodCreateRequest;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.meal.service.MealFoodService;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealFoodSeederService {

  private final MealRepository mealRepository;
  private final MealFoodService mealFoodService;

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

  private static final List<String> CALORIE_UNITS = List.of("kcal");

  private static final List<String> PICTURES =
      List.of(
          "https://picsum.photos/200", "https://picsum.photos/201", "https://picsum.photos/202");

  private final Random random = new Random();

  public Mono<Void> seed() {
    log.info("Starting Food seeding...");

    return mealRepository
        .findAll()
        .flatMap(
            meal ->
                mealFoodService
                    .countByMealIdAndFilter(meal.getId(), new MealFoodFilter())
                    .flatMap(
                        count -> {
                          if (count > 0) {
                            return Mono.empty();
                          }

                          int foodCount = ThreadLocalRandom.current().nextInt(5, 11); // 5–10 foods
                          List<Mono<?>> creations = new ArrayList<>();

                          for (int i = 0; i < foodCount; i++) {
                            MealFoodCreateRequest dto =
                                new MealFoodCreateRequest(
                                    randomFoodName(),
                                    "Info about " + i,
                                    "Detailed info about " + i,
                                    randomPicture(),
                                    randomCalorieAmount(),
                                    CALORIE_UNITS.get(0),
                                    randomServings(),
                                    randomNutritions());

                            creations.add(
                                mealFoodService
                                    .create(dto, meal.getId())
                                    .doOnSuccess(
                                        food ->
                                            log.info(
                                                "Seeded food '{}' for meal '{}'",
                                                food.name(),
                                                meal.getName())));
                          }

                          return Mono.when(creations);
                        }))
        .doOnTerminate(() -> log.info("Food seeding completed."))
        .then();
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
      boolean isMain = (i == 0); // ensure at least one main
      servings.add(
          new ServingCreateRequest(
              10.0 + random.nextDouble() * 200.0, metrics.get(i % metrics.size()).name(), isMain));
    }
    return servings;
  }

  private Set<NutritionCreateRequest> randomNutritions() {
    int count = ThreadLocalRandom.current().nextInt(5, 21); // 5–20 nutrients
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

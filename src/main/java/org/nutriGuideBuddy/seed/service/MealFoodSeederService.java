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
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest; // <-- using String metric DTO
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.nutriGuideBuddy.features.shared.enums.CalorieUnits;
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
                                      if (count > 0) {
                                        return Flux.empty();
                                      }

                                      int foodCount =
                                          ThreadLocalRandom.current().nextInt(5, 11); // 5–10 foods
                                      List<Mono<?>> creations = new ArrayList<>();

                                      for (int i = 0; i < foodCount; i++) {
                                        User owner =
                                            users.get(
                                                random.nextInt(users.size())); // random seeded user

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
                                                            "🥑 Seeded food '{}' for meal '{}' (user '{}')",
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

  private Set<NutritionCreateRequest> randomNutritions() {
    int count = ThreadLocalRandom.current().nextInt(5, 21);
    List<AllowedNutrients> nutrients = new ArrayList<>(Arrays.asList(AllowedNutrients.values()));
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

  private Set<ServingCreateRequest> randomServings() {
    Set<ServingCreateRequest> servings = new LinkedHashSet<>();

    List<String> metrics =
        Arrays.asList(
            "GRAM",
            "OUNCE",
            "CUP",
            "TABLESPOON",
            "TEASPOON",
            "PIECE",
            "SLICE",
            "MILLILITER",
            "LITER");

    String mainMetric = metrics.get(random.nextInt(metrics.size()));
    double mainAmount = randomServingAmount(mainMetric);
    double mainGramsTotal = computeGramsTotal(mainMetric, mainAmount);

    if (mainAmount < 0.1 || mainGramsTotal < 0.1 || mainMetric.length() > 50) {
      mainMetric = "GRAM";
      mainAmount = pickOne(85.0, 100.0, 150.0);
      mainGramsTotal = mainAmount;
    }
    servings.add(new ServingCreateRequest(true, mainMetric, mainAmount, mainGramsTotal));

    int extra = ThreadLocalRandom.current().nextInt(1, 4);
    for (int i = 0; i < extra; i++) {
      String metric = metrics.get(random.nextInt(metrics.size()));
      int attempts = 0;
      while (attempts++ < 5 && metric.equalsIgnoreCase(mainMetric)) {
        metric = metrics.get(random.nextInt(metrics.size()));
      }

      double amount = randomServingAmount(metric);
      double gramsTotal = computeGramsTotal(metric, amount);

      if (amount >= 0.1 && gramsTotal >= 0.1 && metric.length() <= 50) {
        servings.add(new ServingCreateRequest(false, metric, amount, gramsTotal));
      }
    }

    boolean hasMassBased =
        servings.stream()
            .anyMatch(
                s -> "GRAM".equalsIgnoreCase(s.metric()) || "OUNCE".equalsIgnoreCase(s.metric()));
    if (!hasMassBased) {
      double gAmt = pickOne(85.0, 100.0, 150.0);
      servings.add(new ServingCreateRequest(false, "GRAM", gAmt, gAmt));
    }

    return servings;
  }

  private double randomServingAmount(String metric) {
    return switch (metric) {
      case "GRAM" -> pickOne(50.0, 100.0, 200.0);
      case "OUNCE" -> pickOne(1.0, 3.0, 8.0);
      case "CUP" -> pickOne(0.5, 1.0);
      case "TABLESPOON", "TEASPOON" -> pickOne(1.0, 2.0, 3.0);
      case "PIECE", "SLICE" -> pickOne(1.0, 2.0, 3.0, 4.0);
      case "MILLILITER" -> pickOne(50.0, 100.0, 250.0);
      case "LITER" -> pickOne(0.25, 0.5, 1.0);
      default -> 1.0;
    };
  }

  private double computeGramsTotal(String metric, double amount) {
    return switch (metric) {
      case "GRAM" -> amount;
      case "OUNCE" -> amount * 28.3495;
      case "MILLILITER" -> {
        double density = randomDensity();
        yield amount * density;
      }
      case "LITER" -> {
        double density = randomDensity();
        yield amount * 1000.0 * density;
      }
      case "TEASPOON" -> {
        double density = randomDensity();
        yield amount * 5.0 * density;
      }
      case "TABLESPOON" -> {
        double density = randomDensity();
        yield amount * 15.0 * density;
      }
      case "CUP" -> {
        double density = randomDensity();
        yield amount * 240.0 * density;
      }
      case "PIECE" -> amount * pickOne(8.0, 15.0, 30.0);
      case "SLICE" -> amount * pickOne(20.0, 30.0, 40.0);
      default -> Math.max(0.1, amount);
    };
  }

  private double randomDensity() {
    return 0.9 + random.nextDouble() * 0.2;
  }

  @SafeVarargs
  private final <T> T pickOne(T... options) {
    return options[random.nextInt(options.length)];
  }
}

package org.nutriGuideBuddy.seed.development.utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.nutriGuideBuddy.features.shared.dto.NutritionCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.enums.AllowedNutrients;
import org.springframework.stereotype.Component;

@Component
public class FoodSeedUtils {

  private final Random random = new Random();

  public static final List<String> METRICS =
      List.of(
          "Gram",
          "Ounce",
          "Cup",
          "Tablespoon",
          "Teaspoon",
          "Piece",
          "Slice",
          "Milliliter",
          "Liter");

  public double randomCalorieAmount() {
    return 50.0 + random.nextDouble() * 500.0;
  }

  public Set<NutritionCreateRequest> randomNutritions() {
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

  private double randomNutritionAmount(String unit) {
    return switch (unit) {
      case "g" -> 0.1 + random.nextDouble() * 50.0;
      case "mg" -> 1.0 + random.nextDouble() * 500.0;
      case "µg", "μg" -> 5.0 + random.nextDouble() * 1000.0;
      case "L" -> 0.1 + random.nextDouble() * 2.0;
      default -> 1.0;
    };
  }

  /** Generate a random set of servings (exactly one main) */
  public Set<ServingCreateRequest> randomServings() {
    Set<ServingCreateRequest> servings = new LinkedHashSet<>();

    // main serving
    String mainMetric = METRICS.get(random.nextInt(METRICS.size()));
    double mainAmount = randomServingAmount(mainMetric);
    double mainGramsTotal = computeGramsTotal(mainMetric, mainAmount);

    if (mainAmount < 0.1 || mainGramsTotal < 0.1 || mainMetric.length() > 50) {
      mainMetric = "Gram";
      mainAmount = pickOne(85.0, 100.0, 150.0);
      mainGramsTotal = mainAmount;
    }
    servings.add(new ServingCreateRequest(true, mainMetric, mainAmount, mainGramsTotal));

    // extras
    int extra = ThreadLocalRandom.current().nextInt(1, 4);
    for (int i = 0; i < extra; i++) {
      String metric = METRICS.get(random.nextInt(METRICS.size()));
      int attempts = 0;
      while (attempts++ < 5 && metric.equalsIgnoreCase(mainMetric)) {
        metric = METRICS.get(random.nextInt(METRICS.size()));
      }

      double amount = randomServingAmount(metric);
      double gramsTotal = computeGramsTotal(metric, amount);

      if (amount >= 0.1 && gramsTotal >= 0.1 && metric.length() <= 50) {
        servings.add(new ServingCreateRequest(false, metric, amount, gramsTotal));
      }
    }

    // ensure at least one mass-based metric
    boolean hasMassBased =
        servings.stream()
            .anyMatch(
                s -> "Gram".equalsIgnoreCase(s.metric()) || "Ounce".equalsIgnoreCase(s.metric()));
    if (!hasMassBased) {
      double gAmt = pickOne(85.0, 100.0, 150.0);
      servings.add(new ServingCreateRequest(false, "Gram", gAmt, gAmt));
    }

    return servings;
  }

  private double randomServingAmount(String metric) {
    return switch (metric) {
      case "Gram" -> pickOne(50.0, 100.0, 200.0);
      case "Ounce" -> pickOne(1.0, 3.0, 8.0);
      case "Cup" -> pickOne(0.5, 1.0);
      case "Tablespoon", "Teaspoon" -> pickOne(1.0, 2.0, 3.0);
      case "Piece", "Slice" -> pickOne(1.0, 2.0, 3.0, 4.0);
      case "Milliliter" -> pickOne(50.0, 100.0, 250.0);
      case "Liter" -> pickOne(0.25, 0.5, 1.0);
      default -> 1.0;
    };
  }

  private double computeGramsTotal(String metric, double amount) {
    return switch (metric) {
      case "Gram" -> amount;
      case "Ounce" -> amount * 28.3495;
      case "Milliliter" -> amount * randomDensity();
      case "Liter" -> amount * 1000.0 * randomDensity();
      case "Teaspoon" -> amount * 5.0 * randomDensity();
      case "Tablespoon" -> amount * 15.0 * randomDensity();
      case "Cup" -> amount * 240.0 * randomDensity();
      case "Piece" -> amount * pickOne(8.0, 15.0, 30.0);
      case "Slice" -> amount * pickOne(20.0, 30.0, 40.0);
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

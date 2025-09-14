package org.nutriGuideBuddy.features.meal.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.repository.projetion.*;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomMealRepository {

  private final DatabaseClient client;

  public Flux<MealProjection> findAllMealsWithShortFoodDetails() {
    String sql =
        """
        SELECT
            m.id AS meal_id,
            m.name AS meal_name,
            m.user_id AS user_id, -- Ensure user_id is selected
            f.id AS food_id,
            f.name AS food_name,
            c.amount AS calorie_amount,
            c.unit AS calorie_unit
        FROM meals m
        LEFT JOIN inserted_foods f ON f.meal_id = m.id
        LEFT JOIN calories c ON c.meal_id = m.id AND c.food_id = f.id
        ORDER BY m.id, f.id, c.id
    """;

    return client
        .sql(sql)
        .map(
            (row, metadata) ->
                new Object[] {
                  row.get("meal_id", Long.class),
                  row.get("meal_name", String.class),
                  row.get("user_id", Long.class),
                  row.get("food_id", Long.class),
                  row.get("food_name", String.class),
                  row.get("calorie_amount", BigDecimal.class),
                  row.get("calorie_unit", String.class)
                })
        .all()
        .collectList()
        .flatMapMany(
            rows -> {
              Map<Long, MealProjection> mealMap = new LinkedHashMap<>();

              for (Object[] r : rows) {
                Long mealId = (Long) r[0];
                String mealName = (String) r[1];
                Long userId = (Long) r[2]; // Extract userId from row
                Long foodId = (Long) r[3];
                String foodName = (String) r[4];
                BigDecimal calorieAmount = (BigDecimal) r[5];
                String calorieUnit = (String) r[6];

                // Meal level
                MealProjection meal =
                    mealMap.computeIfAbsent(
                        mealId,
                        id ->
                            new MealProjection(
                                mealId,
                                userId, // Set userId
                                mealName,
                                new ArrayList<>(),
                                new ArrayList<>()));

                // Food level
                if (foodId != null) {
                  FoodShortProjection food =
                      meal.getFoods().stream()
                          .filter(f -> f.getId().equals(foodId))
                          .findFirst()
                          .orElseGet(
                              () -> {
                                FoodShortProjection f =
                                    new FoodShortProjection(foodId, foodName, BigDecimal.ZERO);
                                meal.getFoods().add(f);
                                return f;
                              });

                  // Aggregate calories for this food
                  if (calorieAmount != null) {
                    food.setCalories(
                        food.getCalories() != null
                            ? food.getCalories().add(calorieAmount)
                            : calorieAmount);
                  }

                  // Also add meal-level calorie if needed
                  if (calorieAmount != null) {
                    CalorieProjection mealCalorie =
                        new CalorieProjection(null, calorieAmount, calorieUnit);
                    boolean alreadyExists =
                        meal.getCalories().stream()
                            .anyMatch(
                                c ->
                                    c.getAmount().equals(calorieAmount)
                                        && Objects.equals(c.getUnit(), calorieUnit));
                    if (!alreadyExists) {
                      meal.getCalories().add(mealCalorie);
                    }
                  }
                }
              }

              return Flux.fromIterable(mealMap.values());
            });
  }

  public Mono<MealProjection> findById(Long mealId) {
    String sql =
        """
        SELECT
            m.id AS meal_id,
            m.name AS meal_name,
            m.user_id AS user_id, -- Ensure user_id is selected
            f.id AS food_id,
            f.name AS food_name,
            c.amount AS calorie_amount,
            c.unit AS calorie_unit
        FROM meals m
        LEFT JOIN inserted_foods f ON f.meal_id = m.id
        LEFT JOIN calories c ON c.meal_id = m.id AND c.food_id = f.id
        WHERE m.id = :mealId
        ORDER BY f.id, c.id
    """;

    return client
        .sql(sql)
        .bind("mealId", mealId)
        .map(
            (row, metadata) ->
                new Object[] {
                  row.get("meal_id", Long.class),
                  row.get("meal_name", String.class),
                  row.get("user_id", Long.class), // Extract user_id
                  row.get("food_id", Long.class),
                  row.get("food_name", String.class),
                  row.get("calorie_amount", BigDecimal.class),
                  row.get("calorie_unit", String.class)
                })
        .all()
        .collectList()
        .flatMap(
            rows -> {
              if (rows.isEmpty()) return Mono.empty();

              // Extract initial data
              Long initialMealId = (Long) rows.get(0)[0];
              String initialMealName = (String) rows.get(0)[1];
              Long initialUserId = (Long) rows.get(0)[2];

              final MealProjection meal =
                  new MealProjection(
                      initialMealId,
                      initialUserId, // Set userId
                      initialMealName,
                      new ArrayList<>(),
                      new ArrayList<>());

              for (Object[] r : rows) {
                Long foodId = (Long) r[3];
                String foodName = (String) r[4];
                BigDecimal calorieAmount = (BigDecimal) r[5];
                String calorieUnit = (String) r[6];

                if (foodId != null) {
                  // Find existing food or add a new one
                  FoodShortProjection food =
                      meal.getFoods().stream()
                          .filter(f -> f.getId().equals(foodId))
                          .findFirst()
                          .orElseGet(
                              () -> {
                                FoodShortProjection f =
                                    new FoodShortProjection(foodId, foodName, BigDecimal.ZERO);
                                meal.getFoods().add(f);
                                return f;
                              });

                  // Aggregate calories for this food
                  if (calorieAmount != null) {
                    // Assuming FoodShortProjection has a field `calories` as BigDecimal
                    food.setCalories(
                        food.getCalories() != null
                            ? food.getCalories().add(calorieAmount)
                            : calorieAmount);
                  }

                  // Also add meal-level calorie if needed
                  if (calorieAmount != null && calorieUnit != null) {
                    CalorieProjection mealCalorie =
                        new CalorieProjection(null, calorieAmount, calorieUnit);
                    boolean alreadyExists =
                        meal.getCalories().stream()
                            .anyMatch(
                                c ->
                                    c.getAmount().equals(calorieAmount)
                                        && Objects.equals(c.getUnit(), calorieUnit));
                    if (!alreadyExists) {
                      meal.getCalories().add(mealCalorie);
                    }
                  }
                }
              }

              return Mono.just(meal);
            });
  }
}

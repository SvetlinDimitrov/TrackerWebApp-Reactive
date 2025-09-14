package org.nutriGuideBuddy.features.meal.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.repository.projetion.*;
import org.nutriGuideBuddy.features.meal.repository.projection.MealDetailedProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class CustomMealDetailedRepository {

  private final DatabaseClient client;

  public Flux<MealDetailedProjection> findAllMealsWithFoodDetails() {
    String sql =
        """
        SELECT
            m.id AS meal_id,
            m.name AS meal_name,
            f.id AS food_id,
            f.name AS food_name,
            c.id AS calorie_id,
            c.amount AS calorie_amount,
            c.unit AS calorie_unit,
            n.id AS nutrition_id,
            n.name AS nutrition_name,
            n.unit AS nutrition_unit,
            n.amount AS nutrition_amount,
            fi.id AS foodinfo_id,
            fi.info AS food_info,
            fi.large_info AS food_large_info,
            fi.picture AS food_picture,
            s.id AS serving_id,
            s.amount AS serving_amount,
            s.serving_weight AS serving_weight,
            s.metric AS serving_metric,
            s.main AS serving_main
        FROM meals m
        LEFT JOIN inserted_foods f ON f.meal_id = m.id
        LEFT JOIN calories c ON c.meal_id = m.id AND c.food_id = f.id
        LEFT JOIN nutritions n ON n.food_id = f.id
        LEFT JOIN info_foods fi ON fi.food_id = f.id
        LEFT JOIN servings s ON s.food_id = f.id
        ORDER BY m.id, f.id, c.id, n.id, fi.id, s.id
    """;

    return client
        .sql(sql)
        .map(
            (row, metadata) ->
                new Object[] {
                  row.get("meal_id", Long.class),
                  row.get("meal_name", String.class),
                  row.get("food_id", Long.class),
                  row.get("food_name", String.class),
                  row.get("calorie_id", Long.class),
                  row.get("calorie_amount", BigDecimal.class),
                  row.get("calorie_unit", String.class),
                  row.get("nutrition_id", Long.class),
                  row.get("nutrition_name", String.class),
                  row.get("nutrition_unit", String.class),
                  row.get("nutrition_amount", BigDecimal.class),
                  row.get("foodinfo_id", Long.class),
                  row.get("food_info", String.class),
                  row.get("food_large_info", String.class),
                  row.get("food_picture", String.class),
                  row.get("serving_id", Long.class),
                  row.get("serving_amount", BigDecimal.class),
                  row.get("serving_weight", BigDecimal.class),
                  row.get("serving_metric", String.class),
                  row.get("serving_main", Boolean.class)
                })
        .all()
        .collectList()
        .flatMapMany(
            rows -> {
              Map<Long, MealDetailedProjection> mealMap = new LinkedHashMap<>();

              for (Object[] r : rows) {
                Long mealId = (Long) r[0];
                String mealName = (String) r[1];

                Long foodId = (Long) r[2];
                String foodName = (String) r[3];

                Long calorieId = (Long) r[4];
                BigDecimal calorieAmount = (BigDecimal) r[5];
                String calorieUnit = (String) r[6];

                Long nutritionId = (Long) r[7];
                String nutritionName = (String) r[8];
                String nutritionUnit = (String) r[9];
                BigDecimal nutritionAmount = (BigDecimal) r[10];

                Long foodInfoId = (Long) r[11];
                String info = (String) r[12];
                String largeInfo = (String) r[13];
                String picture = (String) r[14];

                Long servingId = (Long) r[15];
                BigDecimal servingAmount = (BigDecimal) r[16];
                BigDecimal servingWeight = (BigDecimal) r[17];
                String servingMetric = (String) r[18];
                Boolean servingMain = (Boolean) r[19];

                // Meal level
                MealDetailedProjection meal =
                    mealMap.computeIfAbsent(
                        mealId,
                        id ->
                            new MealDetailedProjection(
                                mealId, mealName, new ArrayList<>(), new ArrayList<>()));

                // Food level
                if (foodId != null) {
                  FoodProjection food =
                      meal.getFoods().stream()
                          .filter(f -> f.getId().equals(foodId))
                          .findFirst()
                          .orElseGet(
                              () -> {
                                FoodProjection f =
                                    new FoodProjection(
                                        foodId,
                                        foodName,
                                        null,
                                        null,
                                        new ArrayList<>(),
                                        new ArrayList<>());
                                meal.getFoods().add(f);
                                return f;
                              });

                  // Set single calorie for the food
                  if (calorieId != null && food.getCalorie() == null) {
                    food.setCalorie(new CalorieProjection(calorieId, calorieAmount, calorieUnit));
                  }

                  // Add nutritions
                  if (nutritionId != null) {
                    food.getNutritions()
                        .add(
                            new NutritionProjection(
                                nutritionId, nutritionName, nutritionUnit, nutritionAmount));
                  }

                  // Set food info
                  if (foodInfoId != null && food.getFoodInfo() == null) {
                    food.setFoodInfo(new FoodInfoProjection(foodInfoId, info, largeInfo, picture));
                  }

                  // Add servings
                  if (servingId != null
                      && food.getServing().stream().noneMatch(s -> s.getId().equals(servingId))) {
                    food.getServing()
                        .add(
                            new ServingProjection(
                                servingId,
                                servingAmount,
                                servingWeight,
                                servingMetric,
                                servingMain,
                                foodId));
                  }

                  // Also add calorie at meal level
                  if (calorieId != null) {
                    CalorieProjection mealCalorie =
                        new CalorieProjection(calorieId, calorieAmount, calorieUnit);
                    if (meal.getCalories().stream().noneMatch(c -> c.getId().equals(calorieId))) {
                      meal.getCalories().add(mealCalorie);
                    }
                  }
                }
              }

              return Flux.fromIterable(mealMap.values());
            });
  }
}

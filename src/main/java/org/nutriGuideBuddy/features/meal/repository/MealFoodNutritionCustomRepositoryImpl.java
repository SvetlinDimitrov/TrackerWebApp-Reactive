package org.nutriGuideBuddy.features.meal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedDetailedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionConsumedProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MealFoodNutritionCustomRepositoryImpl implements MealFoodNutritionCustomRepository {

  private final DatabaseClient client;

  public Mono<Map<String, MealFoodNutritionConsumedDetailedProjection>> findUserDailyNutrition(
      Long userId, LocalDate date) {

    String sql =
        """
            SELECT n.id     AS nutrition_id,
                   n.name   AS nutrition_name,
                   n.unit   AS nutrition_unit,
                   m.id     AS meal_id,
                   m.name   AS meal_name,
                   mf.id    AS food_id,
                   mf.name  AS food_name,
                   n.amount AS amount
            FROM meal_foods mf
            JOIN meals m ON mf.meal_id = m.id
            JOIN meal_food_nutrition n ON n.food_id = mf.id
            WHERE mf.user_id = :userId
              AND mf.created_at BETWEEN :startDate AND :endDate
            """;

    return fetchAndAggregate(sql, userId, date, null);
  }

  public Mono<Map<LocalDate, Set<MealFoodNutritionConsumedProjection>>>
      findUserNutritionDailyAmounts(
          Long userId, String nutritionName, LocalDate startDate, LocalDate endDate) {

    String sql =
        """
            SELECT DATE(mf.created_at) AS day,
                   m.id    AS meal_id,
                   m.name  AS meal_name,
                   mf.id   AS food_id,
                   mf.name AS food_name,
                   n.amount AS amount
            FROM meal_foods mf
            JOIN meals m ON mf.meal_id = m.id
            JOIN meal_food_nutrition n ON n.food_id = mf.id
            WHERE mf.user_id = :userId
              AND n.name = :nutritionName
              AND mf.created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY - INTERVAL 1 SECOND
            ORDER BY day
            """;

    return client
        .sql(sql)
        .bind("userId", userId)
        .bind("nutritionName", nutritionName)
        .bind("startDate", startDate.atStartOfDay())
        .bind("endDate", endDate.atStartOfDay())
        .map(
            (row, meta) -> {
              Object rawDay = row.get("day");
              LocalDate day;
              if (rawDay instanceof LocalDate ld) {
                day = ld;
              } else if (rawDay instanceof LocalDateTime ldt) {
                day = ldt.toLocalDate();
              } else {
                day = LocalDate.parse(Objects.requireNonNull(rawDay).toString());
              }

              Long mealId = row.get("meal_id", Long.class);
              String mealName = row.get("meal_name", String.class);
              Long foodId = row.get("food_id", Long.class);
              String foodName = row.get("food_name", String.class);
              Double amount = row.get("amount", Double.class);

              var projection =
                  new MealFoodNutritionConsumedProjection(
                      mealId, mealName, foodId, foodName, amount);

              return Map.entry(day, projection);
            })
        .all()
        .collectList()
        .map(
            list -> {
              Map<LocalDate, Set<MealFoodNutritionConsumedProjection>> result =
                  new LinkedHashMap<>();
              LocalDate cursor = startDate;
              while (!cursor.isAfter(endDate)) {
                result.put(cursor, new HashSet<>());
                cursor = cursor.plusDays(1);
              }

              for (var entry : list) {
                result.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).add(entry.getValue());
              }

              return result;
            });
  }

  private Mono<Map<String, MealFoodNutritionConsumedDetailedProjection>> fetchAndAggregate(
      String sql, Long userId, LocalDate dateOrStart, LocalDate endDate) {

    var spec = client.sql(sql).bind("userId", userId);

    if (endDate == null) {
      spec =
          spec.bind("startDate", dateOrStart.atStartOfDay())
              .bind("endDate", dateOrStart.plusDays(1).atStartOfDay().minusNanos(1));
    } else {
      spec =
          spec.bind("startDate", dateOrStart.atStartOfDay())
              .bind("endDate", endDate.plusDays(1).atStartOfDay().minusNanos(1));
    }

    return spec.map(
            (row, meta) -> {
              Long nutritionId = row.get("nutrition_id", Long.class);
              String nutritionName = row.get("nutrition_name", String.class);
              String nutritionUnit = row.get("nutrition_unit", String.class);

              Long mealId = row.get("meal_id", Long.class);
              String mealName = row.get("meal_name", String.class);
              Long foodId = row.get("food_id", Long.class);
              String foodName = row.get("food_name", String.class);
              Double amount = row.get("amount", Double.class);

              var consumed =
                  new MealFoodNutritionConsumedProjection(
                      mealId, mealName, foodId, foodName, amount);

              return new AbstractMap.SimpleEntry<>(
                  nutritionName,
                  new MealFoodNutritionConsumedDetailedProjection(
                      nutritionId, nutritionName, nutritionUnit, Set.of(consumed)));
            })
        .all()
        .collectList()
        .map(
            list -> {
              Map<String, MealFoodNutritionConsumedDetailedProjection> result = new HashMap<>();
              for (var entry : list) {
                result.merge(
                    entry.getKey(),
                    entry.getValue(),
                    (existing, incoming) -> {
                      Set<MealFoodNutritionConsumedProjection> merged =
                          new HashSet<>(existing.getConsumed());
                      merged.addAll(incoming.getConsumed());
                      return new MealFoodNutritionConsumedDetailedProjection(
                          existing.getId(), existing.getName(), existing.getUnit(), merged);
                    });
              }
              return result;
            });
  }
}

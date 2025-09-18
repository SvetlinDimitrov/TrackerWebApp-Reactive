package org.nutriGuideBuddy.features.shared.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionConsumedDetailedProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionConsumedProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomNutritionRepositoryImpl implements CustomNutritionRepository {

  private final DatabaseClient client;

  public Mono<Map<String, NutritionConsumedDetailedProjection>> findUserDailyNutrition(
      Long userId, LocalDate date) {

    String sql =
        """
        SELECT n.id,
               n.name,
               n.unit,
               m.id   AS meal_id,
               m.name AS meal_name,
               mf.id  AS food_id,
               mf.name AS food_name,
               n.amount
        FROM meal_foods mf
        JOIN meals m ON mf.meal_id = m.id
        JOIN meal_foods_nutritions mfn ON mf.id = mfn.meal_food_id
        JOIN nutritions n ON mfn.nutrition_id = n.id
        WHERE mf.user_id = :userId
          AND mf.created_at BETWEEN :startDate AND :endDate
        """;

    return fetchAndAggregate(sql, userId, date, null);
  }

  public Mono<Map<LocalDate, Double>> findUserNutritionDailyAmounts(
      Long userId, String nutritionName, LocalDate startDate, LocalDate endDate) {

    String sql =
        """
        SELECT DATE(mf.created_at) AS day, SUM(n.amount) AS total_amount
        FROM meal_foods mf
        JOIN meal_foods_nutritions mfn ON mf.id = mfn.meal_food_id
        JOIN nutritions n ON mfn.nutrition_id = n.id
        WHERE mf.user_id = :userId
          AND n.name = :nutritionName
          AND mf.created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY - INTERVAL 1 SECOND
        GROUP BY DATE(mf.created_at)
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
                assert rawDay != null;
                day = LocalDate.parse(rawDay.toString());
              }

              Double amount = row.get("total_amount", Double.class);
              if (amount == null) {
                BigDecimal bd = row.get("total_amount", BigDecimal.class);
                amount = bd != null ? bd.doubleValue() : 0.0;
              }

              return Map.entry(day, amount);
            })
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue, LinkedHashMap::new)
        .map(
            aggregated -> {
              Map<LocalDate, Double> filled = new LinkedHashMap<>();
              LocalDate cursor = startDate;
              while (!cursor.isAfter(endDate)) {
                filled.put(cursor, aggregated.getOrDefault(cursor, 0.0));
                cursor = cursor.plusDays(1);
              }
              return filled;
            });
  }

  private Mono<Map<String, NutritionConsumedDetailedProjection>> fetchAndAggregate(
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
              Long nutritionId = row.get("id", Long.class);
              String nutritionName = row.get("name", String.class);
              String nutritionUnit = row.get("unit", String.class);

              Long mealId = row.get("meal_id", Long.class);
              String mealName = row.get("meal_name", String.class);
              Long foodId = row.get("food_id", Long.class);
              String foodName = row.get("food_name", String.class);
              Double amount = row.get("amount", Double.class);

              var consumed =
                  new NutritionConsumedProjection(mealId, mealName, foodId, foodName, amount);

              return new AbstractMap.SimpleEntry<>(
                  nutritionName,
                  new NutritionConsumedDetailedProjection(
                      nutritionId, nutritionName, nutritionUnit, Set.of(consumed)));
            })
        .all()
        .collectList()
        .map(
            list -> {
              Map<String, NutritionConsumedDetailedProjection> result = new HashMap<>();
              for (var entry : list) {
                result.merge(
                    entry.getKey(),
                    entry.getValue(),
                    (existing, incoming) -> {
                      Set<NutritionConsumedProjection> merged =
                          new HashSet<>(existing.getConsumed());
                      merged.addAll(incoming.getConsumed());
                      return new NutritionConsumedDetailedProjection(
                          existing.getId(), existing.getName(), existing.getUnit(), merged);
                    });
              }
              return result;
            });
  }
}

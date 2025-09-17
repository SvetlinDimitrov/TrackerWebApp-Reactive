package org.nutriGuideBuddy.features.shared.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomNutritionRepositoryImpl implements CustomNutritionRepository {

  private final DatabaseClient client;

  @Override
  public Mono<Map<String, NutritionProjection>> findUserDailyNutrition(
      Long userId, LocalDate date) {
    String sql =
        """
        SELECT n.id, n.name, n.unit, n.amount
        FROM meal_foods mf
        JOIN meal_foods_nutritions mfn ON mf.id = mfn.meal_food_id
        JOIN nutritions n ON mfn.nutrition_id = n.id
        WHERE mf.user_id = :userId
          AND DATE(mf.created_at) = :date
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

  private Mono<Map<String, NutritionProjection>> fetchAndAggregate(
      String sql, Long userId, LocalDate dateOrStart, LocalDate endDate) {
    var spec = client.sql(sql).bind("userId", userId);

    if (endDate == null) {
      spec = spec.bind("date", dateOrStart);
    } else {
      spec = spec.bind("startDate", dateOrStart).bind("endDate", endDate);
    }

    return spec.map(
            (row, meta) ->
                new NutritionProjection(
                    row.get("id", Long.class),
                    row.get("name", String.class),
                    row.get("unit", String.class),
                    row.get("amount", Double.class)))
        .all()
        .collectList()
        .map(
            list -> {
              Map<String, NutritionProjection> result = new HashMap<>();
              for (NutritionProjection np : list) {
                result.merge(
                    np.getName(),
                    np,
                    (existing, incoming) ->
                        new NutritionProjection(
                            existing.getId(),
                            existing.getName(),
                            existing.getUnit(),
                            existing.getAmount() + incoming.getAmount()));
              }
              return result;
            });
  }
}

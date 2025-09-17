package org.nutriGuideBuddy.features.shared.repository;

import java.time.LocalDate;
import java.util.HashMap;
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

    return client
        .sql(sql)
        .bind("userId", userId)
        .bind("date", date)
        .map(
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

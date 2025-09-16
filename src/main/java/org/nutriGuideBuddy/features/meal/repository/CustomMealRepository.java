package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMeal;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodShortProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomMealRepository {

  private final DatabaseClient client;

  public Flux<MealProjection> findAllWithFoodDetailsByFilterAndUserId(
      MealFilter filter, Long userId) {

    if (filter == null) filter = new MealFilter();
    if (filter.getPageable() == null) filter.setPageable(new CustomPageableMeal());

    StringBuilder sql =
        new StringBuilder(
            "SELECT "
                + "m.id AS meal_id, "
                + "m.name AS meal_name, "
                + "m.user_id AS user_id, "
                + "m.created_at AS created_at, "
                + "m.updated_at AS updated_at, "
                + "mf.id AS food_id, "
                + "mf.name AS food_name, "
                + "mf.amount AS calories "
                + "FROM meals m "
                + "LEFT JOIN meal_foods mf ON mf.meal_id = m.id "
                + "WHERE 1=1");

    Map<String, Object> binds = new LinkedHashMap<>();

    if (userId != null) {
      sql.append(" AND m.user_id = :userId");
      binds.put("userId", userId);
    }

    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(m.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }

    if (filter.getCreatedAt() != null) {
      sql.append(" AND DATE(m.created_at) = :createdAt");
      binds.put("createdAt", filter.getCreatedAt());
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<String> idsInList = new ArrayList<>(filter.getIdsIn()); // convert Set -> List
      String inParams = IntStream.range(0, idsInList.size())
          .mapToObj(i -> ":idIn" + i)
          .collect(Collectors.joining(", "));
      sql.append(" AND m.id IN (").append(inParams).append(")");
      for (int i = 0; i < idsInList.size(); i++) {
        binds.put("idIn" + i, idsInList.get(i));
      }
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<String> idsNotInList = new ArrayList<>(filter.getIdsNotIn());
      String notInParams = IntStream.range(0, idsNotInList.size())
          .mapToObj(i -> ":idNotIn" + i)
          .collect(Collectors.joining(", "));
      sql.append(" AND m.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < idsNotInList.size(); i++) {
        binds.put("idNotIn" + i, idsNotInList.get(i));
      }
    }

    // Sorting
    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());
    if (!sortMap.isEmpty()) {
      sql.append(" ORDER BY ");
      sql.append(
          sortMap.entrySet().stream()
              .map(
                  e ->
                      "m."
                          + e.getKey()
                          + " "
                          + ("desc".equalsIgnoreCase(e.getValue()) ? "DESC" : "ASC"))
              .collect(Collectors.joining(", ")));
    } else {
      sql.append(" ORDER BY m.name ASC");
    }

    // Pagination
    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
    int pageNumber = Optional.ofNullable(filter.getPageable().getPageNumber()).orElse(0);
    sql.append(" LIMIT ").append(Math.max(1, pageSize));
    sql.append(" OFFSET ").append(Math.max(0, pageNumber) * pageSize);

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> entry : binds.entrySet()) {
      spec = spec.bind(entry.getKey(), entry.getValue());
    }

    return spec.map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMapMany(this::mapRowsToProjections);
  }

  public Mono<Long> countByFilterAndUserId(MealFilter filter, Long userId) {
    if (filter == null) filter = new MealFilter();
    if (filter.getPageable() == null) filter.setPageable(new CustomPageableMeal());

    StringBuilder sql = new StringBuilder("SELECT COUNT(m.id) AS total_count FROM meals m WHERE 1=1");
    Map<String, Object> binds = new LinkedHashMap<>();

    if (userId != null) {
      sql.append(" AND m.user_id = :userId");
      binds.put("userId", userId);
    }

    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(m.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }

    if (filter.getCreatedAt() != null) {
      sql.append(" AND DATE(m.created_at) = :createdAt");
      binds.put("createdAt", filter.getCreatedAt());
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<String> idsInList = new ArrayList<>(filter.getIdsIn());
      String inParams = IntStream.range(0, idsInList.size())
          .mapToObj(i -> ":idIn" + i)
          .collect(Collectors.joining(", "));
      sql.append(" AND m.id IN (").append(inParams).append(")");
      for (int i = 0; i < idsInList.size(); i++) {
        binds.put("idIn" + i, idsInList.get(i));
      }
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<String> idsNotInList = new ArrayList<>(filter.getIdsNotIn());
      String notInParams = IntStream.range(0, idsNotInList.size())
          .mapToObj(i -> ":idNotIn" + i)
          .collect(Collectors.joining(", "));
      sql.append(" AND m.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < idsNotInList.size(); i++) {
        binds.put("idNotIn" + i, idsNotInList.get(i));
      }
    }

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> entry : binds.entrySet()) {
      spec = spec.bind(entry.getKey(), entry.getValue());
    }

    return spec.map((row, metadata) -> row.get("total_count", Long.class))
        .one()
        .defaultIfEmpty(0L);
  }

  public Mono<MealProjection> findById(Long mealId) {
    String sql =
        """
            SELECT
                m.id AS meal_id,
                m.name AS meal_name,
                m.user_id AS user_id,
                m.created_at AS created_at,
                m.updated_at AS updated_at,
                mf.id AS food_id,
                mf.name AS food_name,
                mf.amount AS calories
            FROM meals m
            LEFT JOIN meal_foods mf ON mf.meal_id = m.id
            WHERE m.id = :mealId
            ORDER BY mf.id
            """;

    return client
        .sql(sql)
        .bind("mealId", mealId)
        .map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMap(
            rows -> {
              MealProjection meal = mapRowsToSingleProjection(rows);
              return meal != null ? Mono.just(meal) : Mono.empty();
            });
  }

  private Object[] mapRowToArray(Row row, RowMetadata metadata) {
    return new Object[] {
      row.get("meal_id", Long.class),
      row.get("meal_name", String.class),
      row.get("user_id", Long.class),
      row.get("created_at", Instant.class),
      row.get("updated_at", Instant.class),
      row.get("food_id", Long.class),
      row.get("food_name", String.class),
      row.get("calories", Double.class)
    };
  }

  private MealProjection mapRowsToSingleProjection(List<Object[]> rows) {
    if (rows.isEmpty()) return null;

    Object[] first = rows.get(0);
    Long mealId = (Long) first[0];
    String name = (String) first[1];
    Long userId = (Long) first[2];
    Instant createdAt = (Instant) first[3];
    Instant updatedAt = (Instant) first[4];

    List<MealFoodShortProjection> foods = new ArrayList<>();
    double totalCalories = 0.0;

    for (Object[] r : rows) {
      Long foodId = (Long) r[5];
      String foodName = (String) r[6];
      Double calories = (Double) r[7];

      if (foodId != null) {
        MealFoodShortProjection food =
            new MealFoodShortProjection(foodId, foodName, calories != null ? calories : 0.0);
        foods.add(food);
        totalCalories += calories != null ? calories : 0.0;
      }
    }

    return new MealProjection(mealId, userId, name, createdAt, updatedAt, totalCalories, foods);
  }

  private Flux<MealProjection> mapRowsToProjections(List<Object[]> rows) {
    Map<Long, MealProjection> mealMap = new LinkedHashMap<>();

    for (Object[] r : rows) {
      Long mealId = (Long) r[0];
      String mealName = (String) r[1];
      Long userId = (Long) r[2];
      Instant createdAt = (Instant) r[3];
      Instant updatedAt = (Instant) r[4];
      Long foodId = (Long) r[5];
      String foodName = (String) r[6];
      Double calories = (Double) r[7];

      MealProjection meal =
          mealMap.computeIfAbsent(
              mealId,
              id ->
                  new MealProjection(
                      id, userId, mealName, createdAt, updatedAt, 0.0, new ArrayList<>()));

      if (foodId != null) {
        MealFoodShortProjection food =
            new MealFoodShortProjection(foodId, foodName, calories != null ? calories : 0.0);
        meal.getFoods().add(food);
        meal.setTotalCalories(meal.getTotalCalories() + (calories != null ? calories : 0.0));
      }
    }

    return Flux.fromIterable(mealMap.values());
  }
}

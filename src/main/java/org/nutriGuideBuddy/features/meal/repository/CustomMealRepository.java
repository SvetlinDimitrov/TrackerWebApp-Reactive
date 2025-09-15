package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.repository.projetion.*;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMeal;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
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
                + "f.id AS food_id, "
                + "f.name AS food_name, "
                + "c.amount AS calorie_amount, "
                + "c.unit AS calorie_unit "
                + "FROM meals m "
                + "LEFT JOIN inserted_foods f ON f.meal_id = m.id "
                + "LEFT JOIN calories c ON c.meal_id = m.id AND c.food_id = f.id "
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
      List<String> placeholders = new ArrayList<>();
      int idx = 0;
      for (String val : filter.getIdsIn()) {
        String param = "idsIn" + (idx++);
        placeholders.add(":" + param);
        binds.put(param, val);
      }
      sql.append(" AND m.id IN (").append(String.join(", ", placeholders)).append(")");
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<String> placeholders = new ArrayList<>();
      int idx = 0;
      for (String val : filter.getIdsNotIn()) {
        String param = "idsNotIn" + (idx++);
        placeholders.add(":" + param);
        binds.put(param, val);
      }
      sql.append(" AND m.id NOT IN (").append(String.join(", ", placeholders)).append(")");
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());

    if (!sortMap.isEmpty()) {
      sql.append(" ORDER BY ");
      List<String> orderClauses = new ArrayList<>();
      for (Map.Entry<String, String> entry : sortMap.entrySet()) {
        String field = entry.getKey();
        String dir = "desc".equalsIgnoreCase(entry.getValue()) ? "DESC" : "ASC";
        orderClauses.add("m." + field + " " + dir);
      }
      sql.append(String.join(", ", orderClauses));
    } else {
      sql.append(" ORDER BY m.name ASC"); // default
    }

    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
    int pageNumber = Optional.ofNullable(filter.getPageable().getPageNumber()).orElse(0);
    int limit = Math.max(1, pageSize);
    int offset = Math.max(0, pageNumber) * limit;
    sql.append(" LIMIT :limit OFFSET :offset");
    binds.put("limit", limit);
    binds.put("offset", offset);

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> e : binds.entrySet()) {
      spec = spec.bind(e.getKey(), e.getValue());
    }

    return spec.map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMapMany(this::mapRowsToProjections);
  }

  public Mono<Long> countByFilterAndUserId(MealFilter filter, Long userId) {
    if (filter == null) filter = new MealFilter();
    if (filter.getPageable() == null) filter.setPageable(new CustomPageableMeal());

    StringBuilder sql =
        new StringBuilder("SELECT COUNT(m.id) AS total_count FROM meals m WHERE 1=1");

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
      List<String> placeholders = new ArrayList<>();
      int idx = 0;
      for (String val : filter.getIdsIn()) {
        String param = "idsIn" + (idx++);
        placeholders.add(":" + param);
        binds.put(param, val);
      }
      sql.append(" AND m.id IN (").append(String.join(", ", placeholders)).append(")");
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<String> placeholders = new ArrayList<>();
      int idx = 0;
      for (String val : filter.getIdsNotIn()) {
        String param = "idsNotIn" + (idx++);
        placeholders.add(":" + param);
        binds.put(param, val);
      }
      sql.append(" AND m.id NOT IN (").append(String.join(", ", placeholders)).append(")");
    }

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> e : binds.entrySet()) {
      spec = spec.bind(e.getKey(), e.getValue());
    }

    return spec.map((row, metadata) -> row.get("total_count", Long.class)).one().defaultIfEmpty(0L);
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
        .map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMap(
            rows -> {
              MealProjection meal = mapRowsToSingleProjection(rows);
              return meal != null ? Mono.just(meal) : Mono.empty();
            });
  }

  private MealProjection mapRowsToSingleProjection(List<Object[]> rows) {
    if (rows.isEmpty()) return null;

    Object[] firstRow = rows.get(0);
    Long mealId = (Long) firstRow[0];
    String mealName = (String) firstRow[1];
    Long userId = (Long) firstRow[2];
    Instant createdAt = (Instant) firstRow[3];
    Instant updatedAt = (Instant) firstRow[4];

    MealProjection meal =
        new MealProjection(
            mealId, userId, mealName, createdAt, updatedAt, new ArrayList<>(), new ArrayList<>());

    for (Object[] r : rows) {
      Long foodId = (Long) r[5];
      String foodName = (String) r[6];
      BigDecimal calorieAmount = (BigDecimal) r[7];
      String calorieUnit = (String) r[8];

      if (foodId != null) {
        FoodShortProjection food =
            meal.getFoods().stream()
                .filter(f -> Objects.equals(f.getId(), foodId))
                .findFirst()
                .orElseGet(
                    () -> {
                      FoodShortProjection f =
                          new FoodShortProjection(foodId, foodName, BigDecimal.ZERO);
                      meal.getFoods().add(f);
                      return f;
                    });

        if (calorieAmount != null) {
          food.setCalories(
              food.getCalories() != null ? food.getCalories().add(calorieAmount) : calorieAmount);
        }

        if (calorieAmount != null && calorieUnit != null) {
          CalorieProjection mealCalorie = new CalorieProjection(null, calorieAmount, calorieUnit);
          boolean alreadyExists =
              meal.getCalories().stream()
                  .anyMatch(
                      c ->
                          Objects.equals(c.getAmount(), calorieAmount)
                              && Objects.equals(c.getUnit(), calorieUnit));
          if (!alreadyExists) meal.getCalories().add(mealCalorie);
        }
      }
    }

    return meal;
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
      BigDecimal calorieAmount = (BigDecimal) r[7];
      String calorieUnit = (String) r[8];

      MealProjection meal =
          mealMap.computeIfAbsent(
              mealId,
              id ->
                  new MealProjection(
                      id,
                      userId,
                      mealName,
                      createdAt,
                      updatedAt,
                      new ArrayList<>(),
                      new ArrayList<>()));

      if (foodId != null) {
        FoodShortProjection food =
            meal.getFoods().stream()
                .filter(f -> Objects.equals(f.getId(), foodId))
                .findFirst()
                .orElseGet(
                    () -> {
                      FoodShortProjection f =
                          new FoodShortProjection(foodId, foodName, BigDecimal.ZERO);
                      meal.getFoods().add(f);
                      return f;
                    });

        if (calorieAmount != null) {
          food.setCalories(
              food.getCalories() != null ? food.getCalories().add(calorieAmount) : calorieAmount);
        }

        if (calorieAmount != null && calorieUnit != null) {
          CalorieProjection mealCalorie = new CalorieProjection(null, calorieAmount, calorieUnit);
          boolean alreadyExists =
              meal.getCalories().stream()
                  .anyMatch(
                      c ->
                          Objects.equals(c.getAmount(), calorieAmount)
                              && Objects.equals(c.getUnit(), calorieUnit));
          if (!alreadyExists) meal.getCalories().add(mealCalorie);
        }
      }
    }

    return Flux.fromIterable(mealMap.values());
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
      row.get("calorie_amount", BigDecimal.class),
      row.get("calorie_unit", String.class)
    };
  }
}

package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
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

    // userId filter
    if (userId != null) {
      sql.append(" AND m.user_id = :userId");
      binds.put("userId", userId);
    }

    // name filter
    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(m.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }

    // idsIn
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

    // idsNotIn
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

  private Flux<MealProjection> mapRowsToProjections(List<Object[]> rows) {
    Map<Long, MealProjection> mealMap = new LinkedHashMap<>();

    for (Object[] r : rows) {
      Long mealId = (Long) r[0];
      String mealName = (String) r[1];
      Long userId = (Long) r[2];
      Long foodId = (Long) r[3];
      String foodName = (String) r[4];
      BigDecimal calorieAmount = (BigDecimal) r[5];
      String calorieUnit = (String) r[6];

      MealProjection meal =
          mealMap.computeIfAbsent(
              mealId,
              id -> new MealProjection(id, userId, mealName, new ArrayList<>(), new ArrayList<>()));

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

        // keep unique meal-level calorie entries (amount + unit)
        if (calorieAmount != null) {
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
      row.get("food_id", Long.class),
      row.get("food_name", String.class),
      row.get("calorie_amount", BigDecimal.class),
      row.get("calorie_unit", String.class)
    };
  }
}

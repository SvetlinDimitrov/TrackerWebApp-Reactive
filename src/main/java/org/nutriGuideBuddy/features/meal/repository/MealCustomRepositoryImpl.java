package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMeal;
import org.nutriGuideBuddy.features.meal.dto.MealFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodConsumedProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodShortProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MealCustomRepositoryImpl implements MealCustomRepository {

  private final DatabaseClient client;

  @Override
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
                + "mf.calorie_amount AS calories "
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
      List<Long> idsInList = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, idsInList.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND m.id IN (").append(inParams).append(")");
      for (int i = 0; i < idsInList.size(); i++) {
        binds.put("idIn" + i, idsInList.get(i));
      }
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> idsNotInList = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, idsNotInList.size())
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
                          + ("desc".equalsIgnoreCase(e.getValue()) ? " DESC" : " ASC"))
              .collect(Collectors.joining(", ")));
    } else {
      sql.append(" ORDER BY m.name ASC");
    }

    // Pagination
    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
    int pageNumber = Optional.ofNullable(filter.getPageable().getPageNumber()).orElse(0);
    if (pageSize <= 0) pageSize = 25; // default safety
    sql.append(" LIMIT ").append(pageSize);
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

  @Override
  public Mono<Long> countByFilterAndUserId(MealFilter filter, Long userId) {
    if (filter == null) filter = new MealFilter();

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
      List<Long> idsInList = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, idsInList.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND m.id IN (").append(inParams).append(")");
      for (int i = 0; i < idsInList.size(); i++) {
        binds.put("idIn" + i, idsInList.get(i));
      }
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> idsNotInList = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, idsNotInList.size())
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

    return spec.map((row, metadata) -> row.get("total_count", Long.class)).one().defaultIfEmpty(0L);
  }

  @Override
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
                mf.calorie_amount AS calories
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

  @Override
  public Flux<MealConsumedProjection> findMealsConsumtionWithFoodsByUserIdAndDate(
      Long userId, LocalDate date) {
    String sql =
        "SELECT m.id AS meal_id, m.name AS meal_name, "
            + "       f.id AS food_id, f.name AS food_name, f.calorie_amount AS calories "
            + "FROM meals m "
            + "JOIN meal_foods f ON m.id = f.meal_id "
            + "WHERE m.user_id = :userId AND DATE(f.created_at) = :date";

    return client
        .sql(sql)
        .bind("userId", userId)
        .bind("date", date)
        .map(
            (row, meta) ->
                new Object() {
                  final Long mealId = row.get("meal_id", Long.class);
                  final String mealName = row.get("meal_name", String.class);
                  final MealFoodConsumedProjection food =
                      new MealFoodConsumedProjection(
                          row.get("food_id", Long.class),
                          row.get("food_name", String.class),
                          row.get("calories", Double.class));
                })
        .all()
        .collectList()
        .flatMapMany(
            rows -> {
              Map<Long, MealConsumedProjection> grouped = new HashMap<>();
              for (var r : rows) {
                grouped.compute(
                    r.mealId,
                    (id, existing) -> {
                      if (existing == null) {
                        return new MealConsumedProjection(
                            r.mealId,
                            r.mealName,
                            r.food.getAmount(),
                            new HashSet<>(Collections.singletonList(r.food)));
                      } else {
                        existing.getFoods().add(r.food);
                        existing.setAmount(existing.getAmount() + r.food.getAmount());
                        return existing;
                      }
                    });
              }
              return Flux.fromIterable(grouped.values());
            });
  }

  @Override
  public Mono<Map<LocalDate, Set<MealConsumedProjection>>> findUserCaloriesDailyAmounts(
      Long userId, LocalDate startDate, LocalDate endDate) {

    String sql =
        """
            SELECT DATE(mf.created_at) AS day,
                   m.id   AS meal_id,
                   m.name AS meal_name,
                   mf.id  AS food_id,
                   mf.name AS food_name,
                   mf.calorie_amount AS calories
            FROM meals m
            JOIN meal_foods mf ON mf.meal_id = m.id
            WHERE m.user_id = :userId
              AND mf.created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY - INTERVAL 1 SECOND
            ORDER BY day, meal_id, food_id
            """;

    return client
        .sql(sql)
        .bind("userId", userId)
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
              Double amount = row.get("calories", Double.class);

              MealFoodConsumedProjection food =
                  new MealFoodConsumedProjection(foodId, foodName, amount);
              MealConsumedProjection meal =
                  new MealConsumedProjection(
                      mealId, mealName, amount, new HashSet<>(Collections.singletonList(food)));

              return Map.entry(day, meal);
            })
        .all()
        .collectList()
        .map(
            list -> {
              Map<LocalDate, Set<MealConsumedProjection>> result = new LinkedHashMap<>();
              LocalDate cursor = startDate;
              while (!cursor.isAfter(endDate)) {
                result.put(cursor, new HashSet<>());
                cursor = cursor.plusDays(1);
              }

              for (var entry : list) {
                LocalDate day = entry.getKey();
                MealConsumedProjection incoming = entry.getValue();

                result.computeIfAbsent(day, k -> new HashSet<>()).stream()
                    .filter(m -> m.getId().equals(incoming.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                        existing -> {
                          Set<MealFoodConsumedProjection> foods =
                              new HashSet<>(existing.getFoods());
                          foods.addAll(incoming.getFoods());
                          existing.setFoods(foods);
                          existing.setAmount(
                              foods.stream()
                                  .mapToDouble(MealFoodConsumedProjection::getAmount)
                                  .sum());
                        },
                        () -> result.get(day).add(incoming));
              }

              return result;
            });
  }

  /* ===== helpers ===== */

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

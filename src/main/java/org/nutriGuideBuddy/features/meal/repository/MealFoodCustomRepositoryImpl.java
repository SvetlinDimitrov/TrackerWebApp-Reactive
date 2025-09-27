package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMealFood;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodNutritionProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodServingProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MealFoodCustomRepositoryImpl implements MealFoodCustomRepository {

  private final DatabaseClient client;

  @Override
  public Mono<MealFoodProjection> findById(Long mealFoodId) {
    String sql =
        """
            SELECT
                mf.id   AS meal_food_id,
                mf.name AS meal_food_name,
                mf.info AS meal_food_info,
                mf.large_info AS meal_food_large_info,
                mf.picture AS meal_food_picture,
                mf.calorie_amount AS calorie_amount,
                mf.calorie_unit AS calorie_unit,
                mf.meal_id AS meal_id,

                s.id     AS serving_id,
                s.amount AS serving_amount,
                s.grams_total AS serving_grams_total,
                s.metric AS serving_metric,
                s.main   AS serving_main,

                n.id     AS nutrition_id,
                n.name   AS nutrition_name,
                n.unit   AS nutrition_unit,
                n.amount AS nutrition_amount

            FROM meal_foods mf
            LEFT JOIN meal_food_serving   s ON s.food_id = mf.id
            LEFT JOIN meal_food_nutrition n ON n.food_id = mf.id
            WHERE mf.id = :mealFoodId
            ORDER BY s.id, n.id
            """;

    return client
        .sql(sql)
        .bind("mealFoodId", mealFoodId)
        .map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMap(rows -> rows.isEmpty() ? Mono.empty() : Mono.just(mapRowsToProjection(rows)));
  }

  @Override
  public Flux<MealFoodProjection> findAllByMealIdAndFilter(Long mealId, MealFoodFilter filter) {
    if (filter == null) filter = new MealFoodFilter();
    if (filter.getPageable() == null) filter.setPageable(new CustomPageableMealFood());

    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("mealId", mealId);

    StringBuilder where = new StringBuilder(" WHERE mf.meal_id = :mealId");

    if (filter.getName() != null && !filter.getName().isBlank()) {
      where.append(" AND LOWER(mf.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }
    if (filter.getMinCalorieAmount() != null) {
      where.append(" AND mf.calorie_amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }
    if (filter.getMaxCalorieAmount() != null) {
      where.append(" AND mf.calorie_amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND mf.id IN (").append(inParams).append(")");
      for (int i = 0; i < ids.size(); i++) binds.put("idIn" + i, ids.get(i));
    }
    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idNotIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND mf.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) binds.put("idNotIn" + i, ids.get(i));
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());
    String orderBy =
        sortMap.isEmpty()
            ? "mf.name ASC"
            : sortMap.entrySet().stream()
                .map(
                    e ->
                        "mf."
                            + e.getKey()
                            + ("desc".equalsIgnoreCase(e.getValue()) ? " DESC" : " ASC"))
                .collect(Collectors.joining(", "));

    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
    if (pageSize <= 0) pageSize = 25;
    int pageNumber = Optional.ofNullable(filter.getPageable().getPageNumber()).orElse(0);

    // Step 1: fetch IDs only
    String idQuery =
        "SELECT mf.id FROM meal_foods mf "
            + where
            + " ORDER BY "
            + orderBy
            + " LIMIT :limit OFFSET :offset";

    binds.put("limit", pageSize);
    binds.put("offset", pageNumber * pageSize);

    DatabaseClient.GenericExecuteSpec idSpec = client.sql(idQuery);
    for (Map.Entry<String, Object> entry : binds.entrySet()) {
      idSpec = idSpec.bind(entry.getKey(), entry.getValue());
    }

    return idSpec
        .map((row, meta) -> row.get("id", Long.class))
        .all()
        .collectList()
        .filter(ids -> !ids.isEmpty())
        .flatMapMany(
            ids -> {
              String detailsQuery =
                  """
                    SELECT
                        mf.id   AS meal_food_id,
                        mf.name AS meal_food_name,
                        mf.info AS meal_food_info,
                        mf.large_info AS meal_food_large_info,
                        mf.picture AS meal_food_picture,
                        mf.calorie_amount AS calorie_amount,
                        mf.calorie_unit   AS calorie_unit,
                        mf.meal_id        AS meal_id,

                        s.id     AS serving_id,
                        s.amount AS serving_amount,
                        s.grams_total AS serving_grams_total,
                        s.metric AS serving_metric,
                        s.main   AS serving_main,

                        n.id     AS nutrition_id,
                        n.name   AS nutrition_name,
                        n.unit   AS nutrition_unit,
                        n.amount AS nutrition_amount

                    FROM meal_foods mf
                    LEFT JOIN meal_food_serving   s ON s.food_id = mf.id
                    LEFT JOIN meal_food_nutrition n ON n.food_id = mf.id
                    WHERE mf.id IN ("""
                      + IntStream.range(0, ids.size())
                          .mapToObj(i -> ":mfId" + i)
                          .collect(Collectors.joining(", "))
                      + ") "
                      + "ORDER BY "
                      + orderBy
                      + ", s.id, n.id";

              DatabaseClient.GenericExecuteSpec detailSpec = client.sql(detailsQuery);
              for (int i = 0; i < ids.size(); i++) {
                detailSpec = detailSpec.bind("mfId" + i, ids.get(i));
              }

              return detailSpec
                  .map(this::mapRowToArray)
                  .all()
                  .collectList()
                  .flatMapMany(this::mapRowsToFluxProjections);
            });
  }

  @Override
  public Mono<Long> countByMealIdAndFilter(Long mealId, MealFoodFilter filter) {
    if (filter == null) filter = new MealFoodFilter();

    StringBuilder sql =
        new StringBuilder(
            "SELECT COUNT(mf.id) AS total_count FROM meal_foods mf WHERE mf.meal_id = :mealId");
    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("mealId", mealId);

    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(mf.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }
    if (filter.getMinCalorieAmount() != null) {
      sql.append(" AND mf.calorie_amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }
    if (filter.getMaxCalorieAmount() != null) {
      sql.append(" AND mf.calorie_amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND mf.id IN (").append(inParams).append(")");
      for (int i = 0; i < ids.size(); i++) binds.put("idIn" + i, ids.get(i));
    }
    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idNotIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND mf.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) binds.put("idNotIn" + i, ids.get(i));
    }

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> e : binds.entrySet()) {
      spec = spec.bind(e.getKey(), e.getValue());
    }

    return spec.map((row, metadata) -> row.get("total_count", Long.class)).one().defaultIfEmpty(0L);
  }

  /* ====== mapping helpers ====== */

  private Object[] mapRowToArray(Row row, RowMetadata metadata) {
    return new Object[] {
      row.get("meal_food_id", Long.class), // [0]
      row.get("meal_food_name", String.class), // [1]
      row.get("meal_food_info", String.class), // [2]
      row.get("meal_food_large_info", String.class), // [3]
      row.get("meal_food_picture", String.class), // [4]
      row.get("calorie_amount", Double.class), // [5]
      row.get("calorie_unit", String.class), // [6]
      row.get("meal_id", Long.class), // [7]
      row.get("serving_id", Long.class), // [8]
      row.get("serving_amount", Double.class), // [9]
      row.get("serving_grams_total", Double.class), // [10]
      row.get("serving_metric", String.class), // [11]
      row.get("serving_main", Boolean.class), // [12]
      row.get("nutrition_id", Long.class), // [13]
      row.get("nutrition_name", String.class), // [14]
      row.get("nutrition_unit", String.class), // [15]
      row.get("nutrition_amount", Double.class) // [16]
    };
  }

  private MealFoodProjection mapRowsToProjection(List<Object[]> rows) {
    Object[] first = rows.get(0);

    Long mealFoodId = (Long) first[0];
    String name = (String) first[1];
    String info = (String) first[2];
    String largeInfo = (String) first[3];
    String picture = (String) first[4];
    Double calorieAmount = (Double) first[5];
    String calorieUnit = (String) first[6];
    Long mealId = (Long) first[7];

    List<MealFoodServingProjection> servings = new ArrayList<>();
    List<MealFoodNutritionProjection> nutritions = new ArrayList<>();

    Set<Long> seenServingIds = new HashSet<>();
    Set<Long> seenNutritionIds = new HashSet<>();

    for (Object[] r : rows) {
      Long sId = (Long) r[8];
      if (sId != null && seenServingIds.add(sId)) {
        servings.add(
            new MealFoodServingProjection(
                sId, (Double) r[9], (Double) r[10], (String) r[11], (Boolean) r[12]));
      }

      Long nId = (Long) r[13];
      if (nId != null && seenNutritionIds.add(nId)) {
        nutritions.add(
            new MealFoodNutritionProjection(nId, (String) r[14], (String) r[15], (Double) r[16]));
      }
    }

    var foodProjection = new MealFoodProjection();
    foodProjection.setId(mealFoodId);
    foodProjection.setName(name);
    foodProjection.setInfo(info);
    foodProjection.setLargeInfo(largeInfo);
    foodProjection.setPicture(picture);
    foodProjection.setCalorieAmount(calorieAmount);
    foodProjection.setCalorieUnit(calorieUnit);
    foodProjection.setMealId(mealId);
    foodProjection.setServings(servings);
    foodProjection.setNutrients(nutritions);

    return foodProjection;
  }

  private Flux<MealFoodProjection> mapRowsToFluxProjections(List<Object[]> rows) {
    Map<Long, MealFoodProjection> map = new LinkedHashMap<>();

    for (Object[] r : rows) {
      Long id = (Long) r[0];
      MealFoodProjection mf =
          map.computeIfAbsent(
              id,
              key -> {
                MealFoodProjection p = new MealFoodProjection();
                p.setId(id);
                p.setName((String) r[1]);
                p.setInfo((String) r[2]);
                p.setLargeInfo((String) r[3]);
                p.setPicture((String) r[4]);
                p.setCalorieAmount((Double) r[5]);
                p.setCalorieUnit((String) r[6]);
                p.setMealId((Long) r[7]);
                p.setServings(new ArrayList<>());
                p.setNutrients(new ArrayList<>());
                return p;
              });

      Long sId = (Long) r[8];
      if (sId != null
          && mf.getServings().stream().noneMatch(sp -> Objects.equals(sp.getId(), sId))) {
        mf.getServings()
            .add(
                new MealFoodServingProjection(
                    sId, (Double) r[9], (Double) r[10], (String) r[11], (Boolean) r[12]));
      }

      Long nId = (Long) r[13];
      if (nId != null
          && mf.getNutrients().stream().noneMatch(np -> Objects.equals(np.getId(), nId))) {
        mf.getNutrients()
            .add(
                new MealFoodNutritionProjection(
                    nId, (String) r[14], (String) r[15], (Double) r[16]));
      }
    }

    return Flux.fromIterable(map.values());
  }
}

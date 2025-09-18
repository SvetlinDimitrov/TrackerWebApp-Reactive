package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMealFood;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.shared.enums.ServingMetric;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.ServingProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomMealFoodRepositoryImpl implements CustomMealFoodRepository {

  private final DatabaseClient client;

  public Mono<MealFoodProjection> findById(Long mealFoodId) {
    String sql =
        """
        SELECT
            mf.id AS meal_food_id,
            mf.name AS meal_food_name,
            mf.info AS meal_food_info,
            mf.large_info AS meal_food_large_info,
            mf.picture AS meal_food_picture,
            mf.calorie_amount AS calorie_amount,
            mf.calorie_unit AS calorie_unit,

            s.id AS serving_id,
            s.amount AS serving_amount,
            s.metric AS serving_metric,
            s.main AS serving_main,

            n.id AS nutrition_id,
            n.name AS nutrition_name,
            n.unit AS nutrition_unit,
            n.amount AS nutrition_amount

        FROM meal_foods mf
        LEFT JOIN meal_foods_servings mfs ON mfs.meal_food_id = mf.id
        LEFT JOIN servings s ON s.id = mfs.serving_id
        LEFT JOIN meal_foods_nutritions mfn ON mfn.meal_food_id = mf.id
        LEFT JOIN nutritions n ON n.id = mfn.nutrition_id
        WHERE mf.id = :mealFoodId
        ORDER BY s.id, n.id
        """;

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql).bind("mealFoodId", mealFoodId);

    return spec.map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMap(rows -> rows.isEmpty() ? Mono.empty() : Mono.just(mapRowsToProjection(rows)));
  }

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
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idIn" + i, ids.get(i));
      }
    }
    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idNotIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND mf.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idNotIn" + i, ids.get(i));
      }
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());
    String orderBy;
    if (!sortMap.isEmpty()) {
      orderBy =
          sortMap.entrySet().stream()
              .map(
                  e ->
                      "mf."
                          + e.getKey()
                          + ("desc".equalsIgnoreCase(e.getValue()) ? " DESC" : " ASC"))
              .collect(Collectors.joining(", "));
    } else {
      orderBy = "mf.name ASC";
    }

    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
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
              // Step 2: fetch full details
              String detailsQuery =
                  """
              SELECT
                  mf.id AS meal_food_id,
                  mf.name AS meal_food_name,
                  mf.info AS meal_food_info,
                  mf.large_info AS meal_food_large_info,
                  mf.picture AS meal_food_picture,
                  mf.calorie_amount AS calorie_amount,
                  mf.calorie_unit AS calorie_unit,

                  s.id AS serving_id,
                  s.amount AS serving_amount,
                  s.metric AS serving_metric,
                  s.main AS serving_main,

                  n.id AS nutrition_id,
                  n.name AS nutrition_name,
                  n.unit AS nutrition_unit,
                  n.amount AS nutrition_amount

              FROM meal_foods mf
              LEFT JOIN meal_foods_servings mfs ON mfs.meal_food_id = mf.id
              LEFT JOIN servings s ON s.id = mfs.serving_id
              LEFT JOIN meal_foods_nutritions mfn ON mfn.meal_food_id = mf.id
              LEFT JOIN nutritions n ON n.id = mfn.nutrition_id
              WHERE mf.id IN ("""
                      + IntStream.range(0, ids.size())
                          .mapToObj(i -> ":mfId" + i)
                          .collect(Collectors.joining(", "))
                      + ") ORDER BY mf.name ASC, s.id, n.id";

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
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idIn" + i, ids.get(i));
      }
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsNotIn());
      String notInParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idNotIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND mf.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idNotIn" + i, ids.get(i));
      }
    }

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    for (Map.Entry<String, Object> e : binds.entrySet()) {
      spec = spec.bind(e.getKey(), e.getValue());
    }

    return spec.map((row, metadata) -> row.get("total_count", Long.class)).one().defaultIfEmpty(0L);
  }

  private Object[] mapRowToArray(Row row, RowMetadata metadata) {
    return new Object[] {
      row.get("meal_food_id", Long.class),
      row.get("meal_food_name", String.class),
      row.get("meal_food_info", String.class),
      row.get("meal_food_large_info", String.class),
      row.get("meal_food_picture", String.class),
      row.get("calorie_amount", Double.class),
      row.get("calorie_unit", String.class),
      row.get("serving_id", Long.class),
      row.get("serving_amount", Double.class),
      // ✅ Enum conversion happens later
      row.get("serving_metric", String.class),
      row.get("serving_main", Boolean.class),
      row.get("nutrition_id", Long.class),
      row.get("nutrition_name", String.class),
      row.get("nutrition_unit", String.class),
      row.get("nutrition_amount", Double.class)
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

    List<ServingProjection> servings = new ArrayList<>();
    List<NutritionProjection> nutritions = new ArrayList<>();

    Set<Long> seenServingIds = new HashSet<>();
    Set<Long> seenNutritionIds = new HashSet<>();

    for (Object[] r : rows) {
      // Servings
      Long sId = (Long) r[7];
      if (sId != null && seenServingIds.add(sId)) {
        String metricStr = (String) r[9];
        ServingMetric metric = metricStr != null ? ServingMetric.valueOf(metricStr) : null;

        servings.add(
            new ServingProjection(
                sId,
                (Double) r[8], // amount
                metric,
                (Boolean) r[10] // main
                ));
      }

      // Nutritions
      Long nId = (Long) r[11];
      if (nId != null && seenNutritionIds.add(nId)) {
        nutritions.add(
            new NutritionProjection(nId, (String) r[12], (String) r[13], (Double) r[14]));
      }
    }

    return new MealFoodProjection(
        mealFoodId,
        name,
        info,
        largeInfo,
        picture,
        calorieAmount,
        calorieUnit,
        servings,
        nutritions);
  }

  private Flux<MealFoodProjection> mapRowsToFluxProjections(List<Object[]> rows) {
    Map<Long, MealFoodProjection> map = new LinkedHashMap<>();

    for (Object[] r : rows) {
      Long id = (Long) r[0];
      MealFoodProjection mf =
          map.computeIfAbsent(
              id,
              key ->
                  new MealFoodProjection(
                      id,
                      (String) r[1],
                      (String) r[2],
                      (String) r[3],
                      (String) r[4],
                      (Double) r[5],
                      (String) r[6],
                      new ArrayList<>(),
                      new ArrayList<>()));

      // Servings
      Long sId = (Long) r[7];
      if (sId != null
          && mf.getServings().stream().noneMatch(sp -> Objects.equals(sp.getId(), sId))) {
        String metricStr = (String) r[9];
        ServingMetric metric = metricStr != null ? ServingMetric.valueOf(metricStr) : null;

        mf.getServings()
            .add(
                new ServingProjection(
                    sId,
                    (Double) r[8], // amount
                    metric,
                    (Boolean) r[10] // main
                    ));
      }

      // Nutritions
      Long nId = (Long) r[11];
      if (nId != null
          && mf.getNutritions().stream().noneMatch(np -> Objects.equals(np.getId(), nId))) {
        mf.getNutritions()
            .add(new NutritionProjection(nId, (String) r[12], (String) r[13], (Double) r[14]));
      }
    }

    return Flux.fromIterable(map.values());
  }
}

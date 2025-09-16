package org.nutriGuideBuddy.features.meal.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.meal.dto.CustomPageableMealFood;
import org.nutriGuideBuddy.features.meal.dto.MealFoodFilter;
import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.meal.repository.projection.MealFoodProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.NutritionProjection;
import org.nutriGuideBuddy.features.shared.repository.projection.ServingProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomMealFoodRepository {

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
            mf.amount AS calorie_amount,
            mf.unit AS calorie_unit,

            s.amount AS serving_amount,
            s.serving_weight AS serving_weight,
            s.metric AS serving_metric,
            s.main AS serving_main,

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

    StringBuilder sql =
        new StringBuilder(
            """
            SELECT
                mf.id AS meal_food_id,
                mf.name AS meal_food_name,
                mf.info AS meal_food_info,
                mf.large_info AS meal_food_large_info,
                mf.picture AS meal_food_picture,
                mf.amount AS calorie_amount,
                mf.unit AS calorie_unit,

                s.amount AS serving_amount,
                s.serving_weight AS serving_weight,
                s.metric AS serving_metric,
                s.main AS serving_main,

                n.name AS nutrition_name,
                n.unit AS nutrition_unit,
                n.amount AS nutrition_amount

            FROM meal_foods mf
            LEFT JOIN meal_foods_servings mfs ON mfs.meal_food_id = mf.id
            LEFT JOIN servings s ON s.id = mfs.serving_id
            LEFT JOIN meal_foods_nutritions mfn ON mfn.meal_food_id = mf.id
            LEFT JOIN nutritions n ON n.id = mfn.nutrition_id
            WHERE mf.meal_id = :mealId
            """);

    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("mealId", mealId);

    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(mf.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }

    if (filter.getMinCalorieAmount() != null) {
      sql.append(" AND mf.amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }

    if (filter.getMaxCalorieAmount() != null) {
      sql.append(" AND mf.amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      sql.append(" AND mf.id IN (:idsIn)");
      binds.put("idsIn", filter.getIdsIn());
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      sql.append(" AND mf.id NOT IN (:idsNotIn)");
      binds.put("idsNotIn", filter.getIdsNotIn());
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());
    if (!sortMap.isEmpty()) {
      sql.append(" ORDER BY ");
      sql.append(
          sortMap.entrySet().stream()
              .map(
                  e ->
                      "mf."
                          + e.getKey()
                          + " "
                          + ("desc".equalsIgnoreCase(e.getValue()) ? "DESC" : "ASC"))
              .collect(Collectors.joining(", ")));
    } else {
      sql.append(" ORDER BY mf.name ASC");
    }

    CustomPageableMealFood pageable = filter.getPageable();
    int pageSize = Optional.ofNullable(pageable.getPageSize()).orElse(25);
    int pageNumber = Optional.ofNullable(pageable.getPageNumber()).orElse(0);
    sql.append(" LIMIT :limit OFFSET :offset");
    binds.put("limit", Math.max(1, pageSize));
    binds.put("offset", Math.max(0, pageNumber) * pageSize);

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    binds.forEach(spec::bind);

    return spec.map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMapMany(this::mapRowsToFluxProjections);
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
      sql.append(" AND mf.amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }

    if (filter.getMaxCalorieAmount() != null) {
      sql.append(" AND mf.amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      sql.append(" AND mf.id IN (:idsIn)");
      binds.put("idsIn", filter.getIdsIn());
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      sql.append(" AND mf.id NOT IN (:idsNotIn)");
      binds.put("idsNotIn", filter.getIdsNotIn());
    }

    DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
    binds.forEach(spec::bind);

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
      row.get("serving_amount", Double.class),
      row.get("serving_weight", Double.class),
      row.get("serving_metric", String.class),
      row.get("serving_main", Boolean.class),
      row.get("nutrition_name", String.class),
      row.get("nutrition_unit", String.class),
      row.get("nutrition_amount", Double.class)
    };
  }

  private MealFoodProjection mapRowsToProjection(List<Object[]> rows) {
    Object[] first = rows.get(0);
    Long id = (Long) first[0];
    String name = (String) first[1];
    String info = (String) first[2];
    String largeInfo = (String) first[3];
    String picture = (String) first[4];
    Double calorieAmount = (Double) first[5];
    String calorieUnit = (String) first[6];

    List<ServingProjection> servings = new ArrayList<>();
    List<NutritionProjection> nutritions = new ArrayList<>();

    Set<String> servingKeys = new HashSet<>();
    Set<String> nutritionKeys = new HashSet<>();

    for (Object[] r : rows) {
      // Servings
      Double sAmount = (Double) r[7];
      Double sWeight = (Double) r[8];
      String sMetric = (String) r[9];
      Boolean sMain = (Boolean) r[10];
      String servingKey = sAmount + "-" + sWeight + "-" + sMetric + "-" + sMain;
      if (sAmount != null && !servingKeys.contains(servingKey)) {
        servings.add(new ServingProjection(sAmount, sWeight, sMetric, sMain));
        servingKeys.add(servingKey);
      }

      // Nutritions
      String nName = (String) r[11];
      String nUnit = (String) r[12];
      Double nAmount = (Double) r[13];
      String nutritionKey = nName + "-" + nUnit + "-" + nAmount;
      if (nName != null && !nutritionKeys.contains(nutritionKey)) {
        nutritions.add(new NutritionProjection(nName, nUnit, nAmount));
        nutritionKeys.add(nutritionKey);
      }
    }

    return new MealFoodProjection(
        id, name, info, largeInfo, picture, calorieAmount, calorieUnit, servings, nutritions);
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
      Double sAmount = (Double) r[7];
      Double sWeight = (Double) r[8];
      String sMetric = (String) r[9];
      Boolean sMain = (Boolean) r[10];
      if (sAmount != null) {
        ServingProjection sp = new ServingProjection(sAmount, sWeight, sMetric, sMain);
        if (!mf.getServing().contains(sp)) mf.getServing().add(sp);
      }

      // Nutritions
      String nName = (String) r[11];
      String nUnit = (String) r[12];
      Double nAmount = (Double) r[13];
      if (nName != null) {
        NutritionProjection np = new NutritionProjection(nName, nUnit, nAmount);
        if (!mf.getNutritions().contains(np)) mf.getNutritions().add(np);
      }
    }

    return Flux.fromIterable(map.values());
  }
}

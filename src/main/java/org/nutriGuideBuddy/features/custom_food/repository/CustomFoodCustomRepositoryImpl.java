package org.nutriGuideBuddy.features.custom_food.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.custom_food.dto.CustomFoodFilter;
import org.nutriGuideBuddy.features.custom_food.dto.CustomPageableCustomFood;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodNutritionProjection;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodProjection;
import org.nutriGuideBuddy.features.custom_food.repository.projection.CustomFoodServingProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomFoodCustomRepositoryImpl implements CustomFoodCustomRepository {

  private final DatabaseClient client;

  @Override
  public Mono<CustomFoodProjection> findById(Long customFoodId) {
    String sql =
        """
            SELECT
                cf.id   AS custom_food_id,
                cf.name AS custom_food_name,
                cf.info AS custom_food_info,
                cf.large_info AS custom_food_large_info,
                cf.picture AS custom_food_picture,
                cf.calorie_amount AS calorie_amount,
                cf.calorie_unit   AS calorie_unit,

                s.id AS serving_id,
                s.amount AS serving_amount,
                s.grams_total AS serving_grams_total,
                s.metric AS serving_metric,
                s.main AS serving_main,

                n.id AS nutrition_id,
                n.name AS nutrition_name,
                n.unit AS nutrition_unit,
                n.amount AS nutrition_amount

            FROM custom_food cf
            LEFT JOIN custom_food_servings s ON s.food_id = cf.id
            LEFT JOIN custom_food_nutritions n ON n.food_id = cf.id
            WHERE cf.id = :customFoodId
            ORDER BY s.id, n.id
            """;

    return client
        .sql(sql)
        .bind("customFoodId", customFoodId)
        .map(this::mapRowToArray)
        .all()
        .collectList()
        .flatMap(rows -> rows.isEmpty() ? Mono.empty() : Mono.just(mapRowsToProjection(rows)));
  }

  @Override
  public Flux<CustomFoodProjection> findAllByUserIdAndFilter(Long userId, CustomFoodFilter filter) {
    if (filter == null) filter = new CustomFoodFilter();
    if (filter.getPageable() == null) filter.setPageable(new CustomPageableCustomFood());

    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("userId", userId);

    var where = new StringBuilder(" WHERE cf.user_id = :userId");

    if (filter.getName() != null && !filter.getName().isBlank()) {
      where.append(" AND LOWER(cf.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }
    if (filter.getMinCalorieAmount() != null) {
      where.append(" AND cf.calorie_amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }
    if (filter.getMaxCalorieAmount() != null) {
      where.append(" AND cf.calorie_amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND cf.id IN (").append(inParams).append(")");
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
      where.append(" AND cf.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idNotIn" + i, ids.get(i));
      }
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable().getSort()).orElse(Collections.emptyMap());
    String orderBy =
        sortMap.isEmpty()
            ? "cf.name ASC"
            : sortMap.entrySet().stream()
                .map(
                    e ->
                        "cf."
                            + e.getKey()
                            + ("desc".equalsIgnoreCase(e.getValue()) ? " DESC" : " ASC"))
                .collect(Collectors.joining(", "));

    int pageSize = Optional.ofNullable(filter.getPageable().getPageSize()).orElse(25);
    int pageNumber = Optional.ofNullable(filter.getPageable().getPageNumber()).orElse(0);

    // Step 1: fetch IDs
    String idQuery =
        "SELECT cf.id FROM custom_food cf "
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
              // Step 2: fetch details
              String detailsQuery =
                  """
                        SELECT
                            cf.id   AS custom_food_id,
                            cf.name AS custom_food_name,
                            cf.info AS custom_food_info,
                            cf.large_info AS custom_food_large_info,
                            cf.picture AS custom_food_picture,
                            cf.calorie_amount AS calorie_amount,
                            cf.calorie_unit   AS calorie_unit,

                            s.id AS serving_id,
                            s.amount AS serving_amount,
                            s.grams_total AS serving_grams_total,
                            s.metric AS serving_metric,
                            s.main AS serving_main,

                            n.id AS nutrition_id,
                            n.name AS nutrition_name,
                            n.unit AS nutrition_unit,
                            n.amount AS nutrition_amount

                        FROM custom_food cf
                        LEFT JOIN custom_food_servings s ON s.food_id = cf.id
                        LEFT JOIN custom_food_nutritions n ON n.food_id = cf.id
                        WHERE cf.id IN (
                        """
                      + IntStream.range(0, ids.size())
                          .mapToObj(i -> ":cfId" + i)
                          .collect(Collectors.joining(", "))
                      + ") ORDER BY cf.name ASC, s.id, n.id";

              DatabaseClient.GenericExecuteSpec detailSpec = client.sql(detailsQuery);
              for (int i = 0; i < ids.size(); i++) {
                detailSpec = detailSpec.bind("cfId" + i, ids.get(i));
              }

              return detailSpec
                  .map(this::mapRowToArray)
                  .all()
                  .collectList()
                  .flatMapMany(this::mapRowsToFluxProjections);
            });
  }

  @Override
  public Mono<Long> countByUserIdAndFilter(Long userId, CustomFoodFilter filter) {
    if (filter == null) filter = new CustomFoodFilter();

    var sql =
        new StringBuilder(
            "SELECT COUNT(cf.id) AS total_count FROM custom_food cf WHERE cf.user_id = :userId");
    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("userId", userId);

    if (filter.getName() != null && !filter.getName().isBlank()) {
      sql.append(" AND LOWER(cf.name) LIKE :name");
      binds.put("name", "%" + filter.getName().toLowerCase() + "%");
    }
    if (filter.getMinCalorieAmount() != null) {
      sql.append(" AND cf.calorie_amount >= :minCalorie");
      binds.put("minCalorie", filter.getMinCalorieAmount());
    }
    if (filter.getMaxCalorieAmount() != null) {
      sql.append(" AND cf.calorie_amount <= :maxCalorie");
      binds.put("maxCalorie", filter.getMaxCalorieAmount());
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      sql.append(" AND cf.id IN (").append(inParams).append(")");
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
      sql.append(" AND cf.id NOT IN (").append(notInParams).append(")");
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

  /* ---------------- mapping helpers ---------------- */

  private Object[] mapRowToArray(Row row, RowMetadata metadata) {
    return new Object[] {
      row.get("custom_food_id", Long.class), // [0]
      row.get("custom_food_name", String.class), // [1]
      row.get("custom_food_info", String.class), // [2]
      row.get("custom_food_large_info", String.class), // [3]
      row.get("custom_food_picture", String.class), // [4]
      row.get("calorie_amount", Double.class), // [5]
      row.get("calorie_unit", String.class), // [6]
      row.get("serving_id", Long.class), // [7]
      row.get("serving_amount", Double.class), // [8]
      row.get("serving_grams_total", Double.class), // [9]
      row.get("serving_metric", String.class), // [10]
      row.get("serving_main", Boolean.class), // [11]
      row.get("nutrition_id", Long.class), // [12]
      row.get("nutrition_name", String.class), // [13]
      row.get("nutrition_unit", String.class), // [14]
      row.get("nutrition_amount", Double.class) // [15]
    };
  }

  private CustomFoodProjection mapRowsToProjection(List<Object[]> rows) {
    Object[] first = rows.get(0);

    Long id = (Long) first[0];
    String name = (String) first[1];
    String info = (String) first[2];
    String largeInfo = (String) first[3];
    String picture = (String) first[4];
    Double calorieAmount = (Double) first[5];
    String calorieUnit = (String) first[6];

    var servingsList = new ArrayList<CustomFoodServingProjection>();
    var nutritionsList = new ArrayList<CustomFoodNutritionProjection>();

    var seenServingIds = new HashSet<Long>();
    var seenNutritionIds = new HashSet<Long>();

    for (Object[] r : rows) {
      Long sId = (Long) r[7];
      if (sId != null && seenServingIds.add(sId)) {
        servingsList.add(
            new CustomFoodServingProjection(
                sId,
                (Double) r[8], // amount
                (Double) r[9], // gramsTotal
                (String) r[10], // metric
                (Boolean) r[11] // main
                ));
      }

      Long nId = (Long) r[12];
      if (nId != null && seenNutritionIds.add(nId)) {
        nutritionsList.add(
            new CustomFoodNutritionProjection(nId, (String) r[13], (String) r[14], (Double) r[15]));
      }
    }

    var customFoodProjection = new CustomFoodProjection();
    customFoodProjection.setId(id);
    customFoodProjection.setName(name);
    customFoodProjection.setInfo(info);
    customFoodProjection.setLargeInfo(largeInfo);
    customFoodProjection.setPicture(picture);
    customFoodProjection.setCalorieAmount(calorieAmount);
    customFoodProjection.setCalorieUnit(calorieUnit);
    customFoodProjection.setServings(servingsList);
    customFoodProjection.setNutrients(nutritionsList);

    return customFoodProjection;
  }

  private Flux<CustomFoodProjection> mapRowsToFluxProjections(List<Object[]> rows) {
    Map<Long, CustomFoodProjection> map = new LinkedHashMap<>();

    for (Object[] r : rows) {
      Long id = (Long) r[0];
      var customFoodProjection =
          map.computeIfAbsent(
              id,
              key -> {
                var customFoodProjection1 = new CustomFoodProjection();
                customFoodProjection1.setId(id);
                customFoodProjection1.setName((String) r[1]);
                customFoodProjection1.setInfo((String) r[2]);
                customFoodProjection1.setLargeInfo((String) r[3]);
                customFoodProjection1.setPicture((String) r[4]);
                customFoodProjection1.setCalorieAmount((Double) r[5]);
                customFoodProjection1.setCalorieUnit((String) r[6]);
                customFoodProjection1.setServings(new ArrayList<>());
                customFoodProjection1.setNutrients(new ArrayList<>());
                return customFoodProjection1;
              });

      Long sId = (Long) r[7];
      if (sId != null
          && customFoodProjection.getServings().stream()
              .noneMatch(sp -> Objects.equals(sp.getId(), sId))) {
        customFoodProjection
            .getServings()
            .add(
                new CustomFoodServingProjection(
                    sId, (Double) r[8], (Double) r[9], (String) r[10], (Boolean) r[11]));
      }

      Long nId = (Long) r[12];
      if (nId != null
          && customFoodProjection.getNutrients().stream()
              .noneMatch(np -> Objects.equals(np.getId(), nId))) {
        customFoodProjection
            .getNutrients()
            .add(
                new CustomFoodNutritionProjection(
                    nId, (String) r[13], (String) r[14], (Double) r[15]));
      }
    }

    return Flux.fromIterable(map.values());
  }
}

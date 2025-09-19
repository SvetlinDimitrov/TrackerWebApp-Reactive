package org.nutriGuideBuddy.features.user.repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.enums.DietType;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.NutritionAuthority;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserDetailsSnapshotCustomRepositoryImpl
    implements UserDetailsSnapshotCustomRepository {

  private final DatabaseClient client;

  @Override
  public Flux<UserDetailsSnapshotProjection> findAllByFilter(
      Long userId, UserDetailsSnapshotFilter filter) {
    if (filter == null) filter = new UserDetailsSnapshotFilter();

    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("userId", userId);

    StringBuilder where = new StringBuilder(" WHERE uds.user_id = :userId");

    if (filter.getFrom() != null) {
      where.append(" AND uds.created_at >= :from");
      binds.put("from", filter.getFrom().atStartOfDay());
    }
    if (filter.getTo() != null) {
      where.append(" AND uds.created_at <= :to");
      binds.put("to", filter.getTo().atTime(23, 59, 59));
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND uds.id IN (").append(inParams).append(")");
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
      where.append(" AND uds.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idNotIn" + i, ids.get(i));
      }
    }

    Map<String, String> sortMap =
        Optional.ofNullable(filter.getPageable())
            .map(CustomPageable::getSort)
            .orElse(Collections.emptyMap());
    String orderBy;
    if (!sortMap.isEmpty()) {
      orderBy =
          sortMap.entrySet().stream()
              .map(
                  e ->
                      "uds."
                          + e.getKey()
                          + ("desc".equalsIgnoreCase(e.getValue()) ? " DESC" : " ASC"))
              .collect(Collectors.joining(", "));
    } else {
      orderBy = "uds.created_at DESC";
    }

    int pageSize =
        Optional.ofNullable(filter.getPageable()).map(CustomPageable::getPageSize).orElse(25);
    int pageNumber =
        Optional.ofNullable(filter.getPageable()).map(CustomPageable::getPageNumber).orElse(0);

    String idQuery =
        "SELECT uds.id FROM user_details_snapshots uds "
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
                  "SELECT uds.id, uds.kilograms, uds.height, uds.age, uds.workout_state, uds.gender, "
                      + "uds.goal, uds.diet, uds.nutrition_authority, uds.user_id, uds.created_at, uds.updated_at "
                      + "FROM user_details_snapshots uds "
                      + "WHERE uds.id IN ("
                      + IntStream.range(0, ids.size())
                          .mapToObj(i -> ":udsId" + i)
                          .collect(Collectors.joining(", "))
                      + ") ORDER BY "
                      + orderBy;

              DatabaseClient.GenericExecuteSpec detailSpec = client.sql(detailsQuery);
              for (int i = 0; i < ids.size(); i++) {
                detailSpec = detailSpec.bind("udsId" + i, ids.get(i));
              }

              return detailSpec
                  .map(
                      (row, metadata) ->
                          new UserDetailsSnapshotProjection(
                              row.get("id", Long.class),
                              row.get("kilograms", Double.class),
                              row.get("height", Double.class),
                              row.get("age", Integer.class),
                              Optional.ofNullable(row.get("workout_state", String.class))
                                  .map(WorkoutState::valueOf)
                                  .orElse(null),
                              Optional.ofNullable(row.get("gender", String.class))
                                  .map(Gender::valueOf)
                                  .orElse(null),
                              Optional.ofNullable(row.get("goal", String.class))
                                  .map(Goals::valueOf)
                                  .orElse(null),
                              Optional.ofNullable(row.get("diet", String.class))
                                  .map(DietType::valueOf)
                                  .orElse(null),
                              Optional.ofNullable(row.get("nutrition_authority", String.class))
                                  .map(NutritionAuthority::valueOf)
                                  .orElse(null),
                              row.get("user_id", Long.class),
                              row.get("created_at", Instant.class),
                              row.get("updated_at", Instant.class)))
                  .all();
            });
  }

  @Override
  public Mono<Long> countByFilter(Long userId, UserDetailsSnapshotFilter filter) {
    if (filter == null) filter = new UserDetailsSnapshotFilter();

    Map<String, Object> binds = new LinkedHashMap<>();
    binds.put("userId", userId);

    StringBuilder where = new StringBuilder(" WHERE uds.user_id = :userId");

    if (filter.getFrom() != null) {
      where.append(" AND uds.created_at >= :from");
      binds.put("from", filter.getFrom().atStartOfDay());
    }
    if (filter.getTo() != null) {
      where.append(" AND uds.created_at <= :to");
      binds.put("to", filter.getTo().atTime(23, 59, 59));
    }
    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      List<Long> ids = new ArrayList<>(filter.getIdsIn());
      String inParams =
          IntStream.range(0, ids.size())
              .mapToObj(i -> ":idIn" + i)
              .collect(Collectors.joining(", "));
      where.append(" AND uds.id IN (").append(inParams).append(")");
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
      where.append(" AND uds.id NOT IN (").append(notInParams).append(")");
      for (int i = 0; i < ids.size(); i++) {
        binds.put("idNotIn" + i, ids.get(i));
      }
    }

    String countQuery = "SELECT COUNT(*) AS cnt FROM user_details_snapshots uds " + where;

    DatabaseClient.GenericExecuteSpec countSpec = client.sql(countQuery);
    for (Map.Entry<String, Object> entry : binds.entrySet()) {
      countSpec = countSpec.bind(entry.getKey(), entry.getValue());
    }

    return countSpec.map((row, meta) -> row.get("cnt", Long.class)).one();
  }
}

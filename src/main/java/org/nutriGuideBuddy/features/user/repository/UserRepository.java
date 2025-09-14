package org.nutriGuideBuddy.features.user.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserFilter;
import org.nutriGuideBuddy.features.user.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Mono<User> save(User entity) {
    return entityTemplate.insert(entity);
  }

  public Flux<User> findAllByFilter(UserFilter filter) {
    var criteria = Criteria.empty();

    if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
      criteria = criteria.and(where("username").like("%" + filter.getUsername() + "%"));
    }

    if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
      criteria = criteria.and(where("email").like("%" + filter.getEmail() + "%"));
    }

    if (filter.getRole() != null && !filter.getRole().isBlank()) {
      criteria = criteria.and(where("user_role").is(filter.getRole()));
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      criteria = criteria.and(where("id").in(filter.getIdsIn()));
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      criteria = criteria.and(where("id").notIn(filter.getIdsNotIn()));
    }

    var constructedQuery = query(criteria);

    var pageable = filter.getPageable();
    for (Map.Entry<String, String> entry : pageable.getSort().entrySet()) {
      String sortField = entry.getKey();
      String sortDirection = entry.getValue().toUpperCase();

      if ("ASC".equals(sortDirection)) {
        constructedQuery = constructedQuery.sort(Sort.by(sortField).ascending());
      } else if ("DESC".equals(sortDirection)) {
        constructedQuery = constructedQuery.sort(Sort.by(sortField).descending());
      } else {
        System.out.println("Invalid sort direction: " + entry.getValue());
      }
    }

    return entityTemplate
        .select(User.class)
        .matching(constructedQuery)
        .all()
        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
        .take(pageable.getPageSize());
  }

  @Modifying
  public Mono<User> update(User updatedEntity) {
    Map<SqlIdentifier, Object> fieldMap = new HashMap<>();
    fieldMap.put(SqlIdentifier.unquoted("username"), updatedEntity.getUsername());
    fieldMap.put(SqlIdentifier.unquoted("email"), updatedEntity.getEmail());
    fieldMap.put(SqlIdentifier.unquoted("password"), updatedEntity.getPassword());

    return entityTemplate
        .update(User.class)
        .matching(query(where("id").is(updatedEntity.getId())))
        .apply(Update.from(fieldMap))
        .then(findById(updatedEntity.getId()));
  }

  public Mono<User> findById(String id) {
    return entityTemplate.selectOne(query(where("id").is(id)), User.class);
  }

  public Mono<User> findByEmail(String email) {
    return entityTemplate.selectOne(query(where("email").is(email)), User.class);
  }

  @Modifying
  public Mono<Void> deleteUserById(String id) {
    return entityTemplate.delete(User.class).matching(query(where("id").is(id))).all().then();
  }

  public Mono<Boolean> existsByEmail(String email) {
    return entityTemplate
        .selectOne(query(where("email").is(email)), User.class)
        .map(user -> true)
        .defaultIfEmpty(false);
  }

  public Flux<User> findByEmails(Set<String> emails) {
    return Flux.fromIterable(emails)
        .flatMap(email -> entityTemplate.selectOne(query(where("email").is(email)), User.class))
        .filter(Objects::nonNull);
  }
}

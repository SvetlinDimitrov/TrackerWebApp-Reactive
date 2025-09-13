package org.nutriGuideBuddy.features.user.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.entity.User;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
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

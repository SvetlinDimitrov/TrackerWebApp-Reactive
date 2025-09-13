package org.nutriGuideBuddy.features.user.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.entity.UserEntity;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Mono<UserEntity> save(UserEntity entity) {
    return entityTemplate.insert(entity);
  }

  @Modifying
  public Mono<UserEntity> update(UserEntity updatedEntity) {
    Map<SqlIdentifier, Object> fieldMap = new HashMap<>();
    fieldMap.put(SqlIdentifier.unquoted("username"), updatedEntity.getUsername());
    fieldMap.put(SqlIdentifier.unquoted("email"), updatedEntity.getEmail());
    fieldMap.put(SqlIdentifier.unquoted("password"), updatedEntity.getPassword());

    return entityTemplate
        .update(UserEntity.class)
        .matching(query(where("id").is(updatedEntity.getId())))
        .apply(Update.from(fieldMap))
        .then(findById(updatedEntity.getId()));
  }

  public Mono<UserEntity> findById(String id) {
    return entityTemplate.selectOne(query(where("id").is(id)), UserEntity.class);
  }

  public Mono<UserEntity> findByEmail(String email) {
    return entityTemplate.selectOne(query(where("email").is(email)), UserEntity.class);
  }

  @Modifying
  public Mono<Void> deleteUserById(String id) {
    return entityTemplate.delete(UserEntity.class).matching(query(where("id").is(id))).all().then();
  }

  public Mono<Boolean> existsByEmail(String email) {
    return entityTemplate
        .selectOne(query(where("email").is(email)), UserEntity.class)
        .map(user -> true)
        .defaultIfEmpty(false);
  }
}

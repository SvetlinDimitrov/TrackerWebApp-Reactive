package org.nutriGuideBuddy.features.record.repository;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.entity.Calorie;
import org.nutriGuideBuddy.features.food.entity.Nutrition;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class RecordRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Flux<Calorie> findCalorieByUserId(String userId) {
    return entityTemplate.select(query(where("userId").is(userId)), Calorie.class);
  }

  public Flux<Nutrition> findAllNutritionsByUserId(String userId) {
    return entityTemplate.select(query(where("userId").is(userId)), Nutrition.class);
  }

  public Mono<UserDetails> findUserDetailsByUserId(String userId) {
    return entityTemplate.selectOne(query(where("userId").is(userId)), UserDetails.class);
  }
}

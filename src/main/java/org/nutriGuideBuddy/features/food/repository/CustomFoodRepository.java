package org.nutriGuideBuddy.features.food.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomFoodRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Flux<Food> findAllFoodsByMealId(Long mealId) {
    return entityTemplate.select(query(where("mealId").is(mealId)), Food.class);
  }

  public Mono<Page<Food>> findAllByFoodsByUserIdAndMealIdPageable(
      Long userId, Long mealId, Pageable pageable) {
    return Optional.ofNullable(mealId)
        .map(
            id ->
                entityTemplate.select(
                    query(where("userId").is(userId).and("mealId").is(mealId)), Food.class))
        .orElse(
            entityTemplate.select(
                query(where("userId").is(userId).and("mealId").isNull()), Food.class))
        .skip(pageable.getOffset())
        .take(pageable.getPageSize())
        .collectList()
        .flatMap(
            foodEntities ->
                entityTemplate
                    .count(query(where("userId").is(userId)), Food.class)
                    .map(count -> new PageImpl<>(foodEntities, pageable, count)));
  }
}

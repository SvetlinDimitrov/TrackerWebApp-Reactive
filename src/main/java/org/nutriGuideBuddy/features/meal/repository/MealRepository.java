package org.nutriGuideBuddy.features.meal.repository;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.entity.Calorie;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class MealRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Mono<Meal> saveMeal(Meal entity) {
    return entityTemplate.insert(entity);
  }

  public Mono<Page<Meal>> findAllMealsByUserId(String userId, Pageable pageable) {
    return entityTemplate
        .select(Meal.class)
        .matching(query(where("userId").is(userId)))
        .all()
        .collectList()
        .map(list -> new PageImpl<>(list, pageable, list.size()));
  }

  public Mono<Meal> findMealByIdAndUserId(String id, String userId) {
    return entityTemplate.selectOne(
        query(where("userId").is(userId).and("id").is(id)), Meal.class);
  }

  @Modifying
  public Mono<Void> deleteMealById(String id) {
    return entityTemplate.delete(Meal.class).matching(query(where("id").is(id))).all().then();
  }

  @Modifying
  public Mono<Void> updateMealNameByIdAndUserId(
      String id, String userId, Meal updatedEntity) {
    return entityTemplate
        .update(Meal.class)
        .matching(query(where("id").is(id).and("userId").is(userId)))
        .apply(Update.update("name", updatedEntity.getName()))
        .then();
  }

  public Flux<Calorie> findCalorieByMealId(String mealId) {
    return entityTemplate.select(query(where("mealId").is(mealId)), Calorie.class);
  }

  public Mono<User> findUserById(String id) {
    return entityTemplate.selectOne(query(where("id").is(id)), User.class);
  }
}

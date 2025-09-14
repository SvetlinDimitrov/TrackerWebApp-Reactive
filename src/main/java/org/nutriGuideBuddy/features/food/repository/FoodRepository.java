package org.nutriGuideBuddy.features.food.repository;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.food.entity.*;
import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class FoodRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Mono<Food> findFoodByIdAndMealIdAndUserId(String foodId, String mealId, String userId) {

    return Optional.ofNullable(mealId)
        .map(
            id ->
                entityTemplate.selectOne(
                    query(where("id").is(foodId).and("mealId").is(id).and("userId").is(userId)),
                    Food.class))
        .orElse(
            entityTemplate.selectOne(
                query(where("id").is(foodId).and("mealId").isNull().and("userId").is(userId)),
                Food.class));
  }

  public Flux<Food> findAllFoodsByMealId(String mealId) {
    return entityTemplate.select(query(where("mealId").is(mealId)), Food.class);
  }

  public Flux<Food> findAllByFoodsByUserIdAndMealId(String userId, String mealId) {
    return Optional.ofNullable(mealId)
        .map(
            id ->
                entityTemplate.select(
                    query(where("userId").is(userId).and("mealId").is(mealId)), Food.class))
        .orElse(
            entityTemplate.select(
                query(where("userId").is(userId).and("mealId").isNull()), Food.class));
  }

  public Mono<Page<Food>> findAllByFoodsByUserIdAndMealIdPageable(
      String userId, String mealId, Pageable pageable) {
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

  @Modifying
  public Mono<Void> deleteFoodById(String foodId, String mealId) {

    return Optional.ofNullable(mealId)
        .map(
            id ->
                entityTemplate
                    .delete(Food.class)
                    .matching(query(where("id").is(foodId).and("mealId").is(id)))
                    .all()
                    .then())
        .orElse(
            entityTemplate.delete(Food.class).matching(query(where("id").is(foodId))).all().then());
  }

  public Mono<Food> saveFood(Food entity) {
    return entityTemplate.insert(entity);
  }

  public Mono<Calorie> saveCalorie(Calorie entity) {
    return entityTemplate.insert(entity);
  }

  public Mono<Calorie> findCalorieByFoodId(String foodId, String mealId) {
    return Optional.ofNullable(mealId)
        .map(
            id ->
                entityTemplate.selectOne(
                    query(where("foodId").is(foodId).and("mealId").is(id)), Calorie.class))
        .orElse(
            entityTemplate.selectOne(
                query(where("foodId").is(foodId).and("mealId").isNull()), Calorie.class));
  }

  public Mono<FoodInfo> saveFoodInfo(FoodInfo entity) {
    return entityTemplate.insert(entity);
  }

  public Mono<FoodInfo> findFoodInfoByFoodId(String foodId) {
    return entityTemplate.selectOne(query(where("foodId").is(foodId)), FoodInfo.class);
  }

  public Flux<Nutrition> saveAllNutritions(List<Nutrition> entities) {
    return Flux.fromIterable(entities).flatMapSequential(entityTemplate::insert);
  }

  public Flux<Nutrition> findAllNutritionsByFoodId(String foodId) {
    return entityTemplate.select(query(where("foodId").is(foodId)), Nutrition.class);
  }

  public Flux<Serving> findAllServingsByFoodId(String foodId) {
    return entityTemplate.select(query(where("foodId").is(foodId)), Serving.class);
  }

  public Flux<Serving> saveAllServings(List<Serving> entities) {
    return Flux.fromIterable(entities).flatMapSequential(entityTemplate::insert);
  }

  public Mono<Meal> findMealByIdAndUserId(String id, String userId) {
    return entityTemplate.selectOne(query(where("userId").is(userId).and("id").is(id)), Meal.class);
  }
}

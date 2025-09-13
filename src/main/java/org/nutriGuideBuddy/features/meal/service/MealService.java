package org.nutriGuideBuddy.features.meal.service;

import java.math.BigDecimal;
import java.util.List;

import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.nutriGuideBuddy.features.food.entity.CalorieEntity;
import org.nutriGuideBuddy.features.food.dto.CreateMeal;
import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.food.dto.ShortenFood;
import org.nutriGuideBuddy.features.meal.dto.MealShortView;
import org.nutriGuideBuddy.features.meal.dto.MealView;
import org.nutriGuideBuddy.features.meal.entity.MealEntity;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.features.food.repository.FoodRepository;
import org.nutriGuideBuddy.features.meal.repository.MealRepository;
import org.nutriGuideBuddy.features.food.service.AbstractFoodService;
import org.nutriGuideBuddy.features.meal.utils.MealModifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MealService extends AbstractFoodService {

  private final MealRepository mealRepository;

  public MealService(FoodRepository repository, MealRepository mealRepository) {
    super(repository);
    this.mealRepository = mealRepository;
  }

  public Mono<Page<MealView>> getAllByUserId(Pageable pageable) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mealRepository.findAllMealsByUserId(userId, pageable))
        .flatMap(
            page ->
                Flux.fromIterable(page)
                    .flatMap(this::fetchMealView)
                    .collectList()
                    .map(list -> new PageImpl<>(list, pageable, list.size())));
  }

  public Mono<Page<MealShortView>> getAllByUserIdShorten(Pageable pageable) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mealRepository.findAllMealsByUserId(userId, pageable))
        .flatMap(
            page ->
                Flux.fromIterable(page)
                    .flatMap(this::fetchShortenMealView)
                    .collectList()
                    .map(list -> new PageImpl<>(list, pageable, list.size())));
  }

  public Mono<MealView> getByIdAndUserId(String mealId) {
    return getMealEntityMono(mealId).flatMap(this::fetchMealView);
  }

  public Mono<MealView> createMeal(CreateMeal dto) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(mealRepository::findUserById)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(401))))
        .flatMap(entity -> MealModifier.validateAndUpdateEntity(dto, entity.getId()))
        .flatMap(
            entity ->
                mealRepository
                    .saveMeal(entity)
                    .flatMap(savedEntity -> getByIdAndUserId(savedEntity.getId())));
  }

  public Mono<MealView> modifyMeal(CreateMeal dto, String mealId) {
    return getMealEntityMono(mealId)
        .flatMap(entity -> MealModifier.validateAndUpdateEntity(entity, dto))
        .flatMap(
            entity ->
                mealRepository
                    .updateMealNameByIdAndUserId(entity.getId(), entity.getUserId(), entity)
                    .then(getByIdAndUserId(entity.getId())));
  }

  public Mono<Void> deleteByIdAndUserId(String mealId) {
    return getMealEntityMono(mealId)
        .flatMap(entity -> mealRepository.deleteMealById(entity.getId()));
  }

  private Mono<MealEntity> getMealEntityMono(String mealId) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> mealRepository.findMealByIdAndUserId(mealId, userId))
        .switchIfEmpty(Mono.error(new BadRequestException("No meal found with id: " + mealId)));
  }

  private Mono<MealView> fetchMealView(MealEntity entity) {
    return Mono.just(entity)
        .flatMap(
            mealEntity ->
                Mono.zip(
                    Mono.just(mealEntity),
                    fetchFoodViewsByMealId(mealEntity.getId()),
                    mealRepository.findCalorieByMealId(mealEntity.getId()).collectList()))
        .map(
            tuple ->
                MealView.toView(
                    tuple.getT1(),
                    tuple.getT2(),
                    tuple.getT3().stream()
                        .map(CalorieEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)));
  }

  private Mono<MealShortView> fetchShortenMealView(MealEntity entity) {
    return Mono.just(entity)
        .flatMap(
            mealEntity ->
                Mono.zip(
                    Mono.just(mealEntity),
                    fetchFoodViewsByMealIdShorten(mealEntity.getId()),
                    mealRepository.findCalorieByMealId(mealEntity.getId()).collectList()))
        .map(
            tuple ->
                MealShortView.toView(
                    tuple.getT1(),
                    tuple.getT2(),
                    tuple.getT3().stream()
                        .map(CalorieEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)));
  }

  private Mono<List<FoodView>> fetchFoodViewsByMealId(String mealId) {
    return repository
        .findAllFoodsByMealId(mealId)
        .flatMap(data -> toFoodView(data, mealId))
        .collectList();
  }

  private Mono<List<ShortenFood>> fetchFoodViewsByMealIdShorten(String mealId) {
    return repository
        .findAllFoodsByMealId(mealId)
        .flatMap(data -> toShortenFoodView(data, mealId))
        .collectList();
  }
}

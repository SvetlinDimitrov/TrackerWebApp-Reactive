package org.nutriGuideBuddy.features.custom_food.repository;

import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodNutrition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomFoodNutritionRepository
    extends ReactiveCrudRepository<CustomFoodNutrition, Long> {

  Flux<CustomFoodNutrition> findAllByFoodId(Long foodId);
}

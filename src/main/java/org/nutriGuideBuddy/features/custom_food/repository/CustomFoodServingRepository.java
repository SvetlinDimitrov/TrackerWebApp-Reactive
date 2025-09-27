package org.nutriGuideBuddy.features.custom_food.repository;

import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodServing;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomFoodServingRepository
    extends ReactiveCrudRepository<CustomFoodServing, Long> {

  Flux<CustomFoodServing> findAllByFoodId(Long foodId);
}

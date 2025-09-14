package org.nutriGuideBuddy.features.food.repository;

import org.nutriGuideBuddy.features.food.entity.Nutrition;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface NutrientRepository extends R2dbcRepository<Nutrition, Long> {

  Flux<Nutrition> findAllByFoodId(Long foodId);

  Flux<Nutrition> findAllByUserId(Long userId);
}

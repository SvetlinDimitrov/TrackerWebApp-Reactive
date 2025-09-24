package org.nutriGuideBuddy.features.custom_food.repository;

import java.util.Set;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodNutrition;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CustomFoodNutritionRepository extends R2dbcRepository<CustomFoodNutrition, Long> {

  Flux<CustomFoodNutrition> findByCustomFoodId(Long customFoodId);

  Flux<CustomFoodNutrition> findByCustomFoodIdIn(Set<Long> customFoodIds);
}

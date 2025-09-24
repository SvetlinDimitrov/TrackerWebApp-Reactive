package org.nutriGuideBuddy.features.custom_food.repository;

import java.util.Collection;
import org.nutriGuideBuddy.features.custom_food.entity.CustomFoodServing;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CustomFoodServingRepository extends R2dbcRepository<CustomFoodServing, Long> {

  Flux<CustomFoodServing> findByCustomFoodId(Long customFoodId);

  Flux<CustomFoodServing> findByCustomFoodIdIn(Collection<Long> customFoodIds);
}

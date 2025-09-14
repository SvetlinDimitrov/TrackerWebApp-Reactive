package org.nutriGuideBuddy.features.food.repository;

import org.nutriGuideBuddy.features.food.entity.FoodInfo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FoodInfoRepository extends R2dbcRepository<FoodInfo, Long> {

  Mono<FoodInfo> findByFoodId(Long foodId);
}

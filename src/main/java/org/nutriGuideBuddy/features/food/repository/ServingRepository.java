package org.nutriGuideBuddy.features.food.repository;

import org.nutriGuideBuddy.features.food.entity.Serving;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ServingRepository extends R2dbcRepository<Serving, Long> {

  Flux<Serving> findAllByFoodId(Long foodId);
}

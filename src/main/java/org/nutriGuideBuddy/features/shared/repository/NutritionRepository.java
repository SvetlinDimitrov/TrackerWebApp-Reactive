package org.nutriGuideBuddy.features.shared.repository;

import org.nutriGuideBuddy.features.shared.entity.Nutrition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NutritionRepository extends ReactiveCrudRepository<Nutrition, Long> {}

package org.nutriGuideBuddy.features.shared.repository;

import org.nutriGuideBuddy.features.shared.entity.Serving;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServingRepository extends ReactiveCrudRepository<Serving, Long> {
}
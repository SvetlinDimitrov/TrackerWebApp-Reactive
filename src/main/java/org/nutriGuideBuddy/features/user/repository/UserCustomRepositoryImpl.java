package org.nutriGuideBuddy.features.user.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserFilter;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

  private final R2dbcEntityTemplate entityTemplate;

  public Flux<User> findAllByFilter(UserFilter filter) {
    var criteria = Criteria.empty();
    if (filter == null) {
      filter = new UserFilter();
    }

    criteria = criteria.and(where("role").not(UserRole.GOD));

    if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
      criteria = criteria.and(where("username").like("%" + filter.getUsername() + "%"));
    }

    if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
      criteria = criteria.and(where("email").like("%" + filter.getEmail() + "%"));
    }

    if (filter.getRole() != null) {
      criteria = criteria.and(where("user_role").is(filter.getRole()));
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      criteria = criteria.and(where("id").in(filter.getIdsIn()));
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      criteria = criteria.and(where("id").notIn(filter.getIdsNotIn()));
    }

    var constructedQuery = query(criteria);

    var pageable = filter.getPageable();
    for (Map.Entry<String, String> entry : pageable.getSort().entrySet()) {
      String sortField = entry.getKey();
      String sortDirection = entry.getValue().toUpperCase();

      if ("ASC".equals(sortDirection)) {
        constructedQuery = constructedQuery.sort(Sort.by(sortField).ascending());
      } else if ("DESC".equals(sortDirection)) {
        constructedQuery = constructedQuery.sort(Sort.by(sortField).descending());
      } else {
        System.out.println("Invalid sort direction: " + entry.getValue());
      }
    }

    return entityTemplate
        .select(User.class)
        .matching(constructedQuery)
        .all()
        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
        .take(pageable.getPageSize());
  }

  public Mono<Long> countByFilter(UserFilter filter) {
    var criteria = Criteria.empty();
    if (filter == null) {
      filter = new UserFilter();
    }

    criteria = criteria.and(where("role").not(UserRole.GOD));

    if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
      criteria = criteria.and(where("username").like("%" + filter.getUsername() + "%"));
    }

    if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
      criteria = criteria.and(where("email").like("%" + filter.getEmail() + "%"));
    }

    if (filter.getRole() != null) {
      criteria = criteria.and(where("user_role").is(filter.getRole()));
    }

    if (filter.getIdsIn() != null && !filter.getIdsIn().isEmpty()) {
      criteria = criteria.and(where("id").in(filter.getIdsIn()));
    }

    if (filter.getIdsNotIn() != null && !filter.getIdsNotIn().isEmpty()) {
      criteria = criteria.and(where("id").notIn(filter.getIdsNotIn()));
    }

    return entityTemplate.count(query(criteria), User.class);
  }
}

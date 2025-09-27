package org.nutriGuideBuddy.features.user.service;


import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotFilter;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.entity.UserDetailsSnapshot;
import org.nutriGuideBuddy.features.user.repository.UserDetailsSnapshotCustomRepository;
import org.nutriGuideBuddy.features.user.repository.UserDetailsSnapshotRepository;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.mappers.UserDetailsSnapshotMapper;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsSnapshotServiceImpl implements UserDetailsSnapshotService {

  private final UserDetailsSnapshotRepository repository;
  private final UserDetailsSnapshotCustomRepository customRepository;
  private final UserDetailsSnapshotMapper mapper;

  public Mono<Void> create(UserDetails userDetails) {
    return repository.save(mapper.toEntity(userDetails)).then();
  }

  public Flux<UserDetailsSnapshotProjection> findAllByFilter(UserDetailsSnapshotFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMapMany(userId -> customRepository.findAllByFilter(userId, filter));
  }

  public Mono<Long> countByFilter(UserDetailsSnapshotFilter filter) {
    return ReactiveUserDetailsServiceImpl.getPrincipalId()
        .flatMap(userId -> customRepository.countByFilter(userId, filter));
  }

  public Mono<UserDetailsSnapshotView> get(Long id) {
    return findByIdOrThrow(id).map(mapper::toView);
  }

  public Mono<UserDetailsSnapshot> findByIdOrThrow(Long id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(NotFoundException.byId(UserDetailsSnapshot.class.getSimpleName(), id)));
  }

  public Mono<Boolean> existsByIdAndUserId(Long id, Long userId) {
    return repository.existsByIdAndUserId(id, userId);
  }

  public Mono<UserDetailsSnapshotView> findLatestByUserIdAndDate(Long userId, Instant dateEnd) {
    return repository
        .findLatestByUserIdAndDate(userId, dateEnd)
        .map(mapper::toView)
        .switchIfEmpty(
            Mono.error(
                NotFoundException.by(UserDetailsSnapshot.class.getSimpleName(), "userId", userId)));
  }
}

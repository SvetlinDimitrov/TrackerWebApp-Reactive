package org.nutriGuideBuddy.infrastructure.mappers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nutriGuideBuddy.features.user.dto.UserDetailsSnapshotView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;
import org.nutriGuideBuddy.features.user.entity.UserDetailsSnapshot;
import org.nutriGuideBuddy.features.user.repository.projection.UserDetailsSnapshotProjection;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface UserDetailsSnapshotMapper {

  @Mapping(target = "id", ignore = true)
  UserDetailsSnapshot toEntity(UserDetails source);

  @Mapping(target = "createdAt", expression = "java(toLocalDate(source.getCreatedAt()))")
  @Mapping(target = "updatedAt", expression = "java(toLocalDate(source.getUpdatedAt()))")
  UserDetailsSnapshotView toView(UserDetailsSnapshot source);

  @Mapping(target = "createdAt", expression = "java(toLocalDate(source.getCreatedAt()))")
  @Mapping(target = "updatedAt", expression = "java(toLocalDate(source.getUpdatedAt()))")
  UserDetailsSnapshotView toView(UserDetailsSnapshotProjection source);

  default LocalDate toLocalDate(Instant instant) {
    return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }
}

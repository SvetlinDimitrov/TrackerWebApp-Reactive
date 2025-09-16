package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.shared.dto.ServingCreateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingUpdateRequest;
import org.nutriGuideBuddy.features.shared.dto.ServingView;
import org.nutriGuideBuddy.features.shared.entity.Serving;
import org.nutriGuideBuddy.features.shared.repository.projection.ServingProjection;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServingMapper {

  ServingView toView(ServingProjection projection);

  ServingView toView(Serving entity);

  Serving toEntity(ServingCreateRequest dto);

  @Mapping(target = "id", ignore = true)
  void update(ServingUpdateRequest request, @MappingTarget Serving entity);
}

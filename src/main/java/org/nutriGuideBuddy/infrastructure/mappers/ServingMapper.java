package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.nutriGuideBuddy.features.food.dto.ServingView;
import org.nutriGuideBuddy.features.food.repository.projetion.ServingProjection;

@Mapper(componentModel = "spring")
public interface ServingMapper {

  ServingView toView(ServingProjection projection);
}

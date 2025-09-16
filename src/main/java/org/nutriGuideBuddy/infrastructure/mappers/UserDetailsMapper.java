package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.features.user.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.entity.UserDetails;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface UserDetailsMapper {

  UserDetailsView toView(UserDetails entity);

  void update(UserDetailsRequest dto, @MappingTarget UserDetails entity);
}

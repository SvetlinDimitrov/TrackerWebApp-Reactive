package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface UserDetailsMapper {

  UserDetailsView toView(UserDetails entity);

  void update(UserDetailsRequest dto, @MappingTarget UserDetails entity);
}

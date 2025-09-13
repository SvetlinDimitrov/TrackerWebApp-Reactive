package org.nutriGuideBuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsRequest;
import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsView;
import org.nutriGuideBuddy.domain.entity.UserDetails;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface UserDetailsMapper {

  UserDetailsView toView(UserDetails entity);

  void update(UserDetailsRequest dto, @MappingTarget UserDetails entity);
}

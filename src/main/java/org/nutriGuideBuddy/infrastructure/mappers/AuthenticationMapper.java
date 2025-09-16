package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UserMapper.class})
@DecoratedWith(AuthenticationMapperDecorator.class)
public interface AuthenticationMapper {

  @Mapping(target = "userView", source = "user")
  AuthenticationResponse toDto(User user, UserDetails userDetails, JwtToken accessToken);
}

package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.nutriGuideBuddy.features.user.entity.UserEntity;

@Mapper(componentModel = "spring")
@DecoratedWith(AuthenticationMapperDecorator.class)
public interface AuthenticationMapper {

  @Mapping(target = "userView", source = "user")
  AuthenticationResponse toDto(UserEntity user, UserDetails userDetails, JwtToken accessToken);
}

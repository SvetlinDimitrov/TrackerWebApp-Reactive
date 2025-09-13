package org.nutriGuideBuddy.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nutriGuideBuddy.domain.dto.auth.AuthenticationResponse;
import org.nutriGuideBuddy.domain.dto.auth.JwtToken;
import org.nutriGuideBuddy.domain.entity.UserDetails;
import org.nutriGuideBuddy.domain.entity.UserEntity;

@Mapper(componentModel = "spring")
@DecoratedWith(AuthenticationMapperDecorator.class)
public interface AuthenticationMapper {

  @Mapping(target = "userView", source = "user")
  AuthenticationResponse toDto(UserEntity user, UserDetails userDetails, JwtToken accessToken);
}

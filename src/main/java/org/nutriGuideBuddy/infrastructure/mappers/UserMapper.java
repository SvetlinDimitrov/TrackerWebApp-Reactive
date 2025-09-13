package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.entity.UserEntity;
import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.dto.UserUpdateRequest;
import org.nutriGuideBuddy.features.user.dto.UserView;
import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

  UserView toView(UserEntity entity);

  UserWithDetailsView toViewWithDetails(UserView user, UserDetailsView userDetails);

  UserEntity toEntity(UserCreateRequest dto, String email);

  void update(ChangePasswordRequest dto, @MappingTarget UserEntity entity);

  void update(UserUpdateRequest dto, @MappingTarget UserEntity entity);
}

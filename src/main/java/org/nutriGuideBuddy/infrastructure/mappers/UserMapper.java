package org.nutriGuideBuddy.infrastructure.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.dto.UserUpdateRequest;
import org.nutriGuideBuddy.features.user.dto.UserView;
import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

  UserView toView(User entity);

  UserWithDetailsView toViewWithDetails(UserView user, UserDetailsView userDetails);

  User toEntity(UserCreateRequest dto, String email);

  void update(ChangePasswordRequest dto, @MappingTarget User entity);

  void update(UserUpdateRequest dto, @MappingTarget User entity);
}

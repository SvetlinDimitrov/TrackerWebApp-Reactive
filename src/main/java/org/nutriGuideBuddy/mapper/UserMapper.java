package org.nutriGuideBuddy.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.nutriGuideBuddy.domain.dto.auth.ChangePasswordRequest;
import org.nutriGuideBuddy.domain.dto.user.*;
import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsView;
import org.nutriGuideBuddy.domain.entity.UserEntity;

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

package org.nutriGuideBuddy.infrastructure.mappers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.mapstruct.*;
import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.dto.UserDetailsView;
import org.nutriGuideBuddy.features.user.dto.UserUpdateRequest;
import org.nutriGuideBuddy.features.user.dto.UserView;
import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDate")
  @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToLocalDate")
  UserView toView(User entity);

  @Mapping(target = "details", source = "userDetails")
  UserWithDetailsView toViewWithDetails(UserView user, UserDetailsView userDetails);

  User toEntity(UserCreateRequest dto, String email);

  void update(ChangePasswordRequest dto, @MappingTarget User entity);

  void update(UserUpdateRequest dto, @MappingTarget User entity);

  @Named("instantToLocalDate")
  static LocalDate instantToLocalDate(Instant instant) {
    if (instant == null) return null;
    return instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }
}

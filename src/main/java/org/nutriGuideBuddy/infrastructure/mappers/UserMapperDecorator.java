package org.nutriGuideBuddy.infrastructure.mappers;

import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public abstract class UserMapperDecorator implements UserMapper {

  private UserMapper delegate;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setDelegate(UserMapper delegate) {
    this.delegate = delegate;
  }

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User toEntity(UserCreateRequest dto , String email) {
    User user = delegate.toEntity(dto , email);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return user;
  }

  @Override
  public void update(ChangePasswordRequest dto, User entity) {
    delegate.update(dto, entity);
    entity.setPassword(passwordEncoder.encode(dto.newPassword()));
  }
}

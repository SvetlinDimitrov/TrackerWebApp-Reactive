package org.nutriGuideBuddy.infrastructure.mappers;

import org.nutriGuideBuddy.features.user.dto.UserCreateRequest;
import org.nutriGuideBuddy.features.user.dto.UserView;
import org.nutriGuideBuddy.features.user.entity.User;
import org.nutriGuideBuddy.infrastructure.security.dto.ChangePasswordRequest;
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
  public UserView toView(User entity) {
    return new UserView(
        String.valueOf(entity.getId()),
        entity.getUsername(),
        entity.getEmail(),
        entity.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        entity.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        entity.getRole());
  }

  @Override
  public User toEntity(UserCreateRequest dto, String email) {
    User user = delegate.toEntity(dto, email);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return user;
  }

  @Override
  public void update(ChangePasswordRequest dto, User entity) {
    delegate.update(dto, entity);
    entity.setPassword(passwordEncoder.encode(dto.password()));
  }
}

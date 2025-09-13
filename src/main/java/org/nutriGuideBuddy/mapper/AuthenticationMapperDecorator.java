package org.nutriGuideBuddy.mapper;

import org.nutriGuideBuddy.domain.dto.auth.AuthenticationResponse;
import org.nutriGuideBuddy.domain.dto.auth.JwtToken;
import org.nutriGuideBuddy.domain.dto.user.UserWithDetailsView;
import org.nutriGuideBuddy.domain.entity.UserDetails;
import org.nutriGuideBuddy.domain.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AuthenticationMapperDecorator implements AuthenticationMapper {

  private UserMapper userMapper;
  private UserDetailsMapper userDetailsMapper;
  private AuthenticationMapper delegate;

  @Autowired
  public void setUserMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Autowired
  public void setUserDetailsMapper(UserDetailsMapper userDetailsMapper) {
    this.userDetailsMapper = userDetailsMapper;
  }

  @Autowired
  public void setDelegate(AuthenticationMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public AuthenticationResponse toDto(
      UserEntity user, UserDetails userDetails, JwtToken accessToken) {
    UserWithDetailsView userWithDetailsView =
        new UserWithDetailsView(userMapper.toView(user), userDetailsMapper.toView(userDetails));
    AuthenticationResponse dto = delegate.toDto(user, userDetails, accessToken);
    return new AuthenticationResponse(userWithDetailsView, dto.accessToken());
  }
}

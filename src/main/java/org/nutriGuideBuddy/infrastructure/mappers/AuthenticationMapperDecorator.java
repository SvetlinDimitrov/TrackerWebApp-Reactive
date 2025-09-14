package org.nutriGuideBuddy.infrastructure.mappers;

import org.nutriGuideBuddy.infrastructure.security.dto.AuthenticationResponse;
import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;
import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;
import org.nutriGuideBuddy.features.user_details.entity.UserDetails;
import org.nutriGuideBuddy.features.user.entity.User;
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
  public AuthenticationResponse toDto(User user, UserDetails userDetails, JwtToken accessToken) {
    UserWithDetailsView userWithDetailsView =
        new UserWithDetailsView(userMapper.toView(user), userDetailsMapper.toView(userDetails));
    AuthenticationResponse dto = delegate.toDto(user, userDetails, accessToken);
    return new AuthenticationResponse(userWithDetailsView, dto.accessToken());
  }
}

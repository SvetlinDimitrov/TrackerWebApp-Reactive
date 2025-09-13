package org.nutriGuideBuddy.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.nutriGuideBuddy.domain.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(UserEntity user, org.nutriGuideBuddy.domain.entity.UserDetails details)
    implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Optional.ofNullable(details.getRole())
        .map(role -> List.of((GrantedAuthority) () -> "ROLE_" + role))
        .orElse(List.of());
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

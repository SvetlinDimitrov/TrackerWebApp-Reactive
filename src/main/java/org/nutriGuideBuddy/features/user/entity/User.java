package org.nutriGuideBuddy.features.user.entity;

import lombok.*;
import org.nutriGuideBuddy.features.shared.entity.BaseEntity;
import org.nutriGuideBuddy.features.user.enums.UserRole;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

  @Column("username")
  private String username;

  @Column("email")
  private String email;

  @Column("password")
  private String password;

  @Column("role")
  private UserRole role = UserRole.USER;
}

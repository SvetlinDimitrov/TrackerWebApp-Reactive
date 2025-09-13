package org.nutriGuideBuddy.domain.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

  @Id private String id = UUID.randomUUID().toString();

  @Column("username")
  private String username;

  @Column("email")
  private String email;

  @Column("password")
  private String password;
}

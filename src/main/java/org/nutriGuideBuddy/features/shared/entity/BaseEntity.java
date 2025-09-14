package org.nutriGuideBuddy.features.shared.entity;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public abstract class BaseEntity {

  @Id private Long id;

  @Column("created_at")
  private Instant createdAt = Instant.now();

  @Column("updated_at")
  private Instant updatedAt = Instant.now();
}

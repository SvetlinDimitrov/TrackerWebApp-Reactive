package org.nutriGuideBuddy.features.shared.entity;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Column;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AuditableEntity extends BaseEntity {

  @Column("created_at")
  private Instant createdAt = Instant.now();

  @Column("updated_at")
  private Instant updatedAt = Instant.now();
}

package org.nutriGuideBuddy.features.shared.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class BaseEntity {

  @Id private Long id;
}

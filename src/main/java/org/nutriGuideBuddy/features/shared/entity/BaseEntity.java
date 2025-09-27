package org.nutriGuideBuddy.features.shared.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

  @Id private Long id;
}

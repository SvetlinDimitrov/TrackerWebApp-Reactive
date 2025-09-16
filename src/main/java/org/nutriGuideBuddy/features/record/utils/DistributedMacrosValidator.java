package org.nutriGuideBuddy.features.record.utils;

import org.nutriGuideBuddy.features.record.dto.DistributedMacros;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import reactor.core.publisher.Mono;

public class DistributedMacrosValidator {

  public static Mono<DistributedMacros> validate(DistributedMacros macros) {
    if (macros == null) {
      return Mono.just(new DistributedMacros(0.25, 0.25, 0.50, 0.07, 0.10));
    }
    if ((macros.fat() == null || macros.fat() >= 1.0)
        || (macros.carbs() == null || macros.carbs() >= 1.0)
        || (macros.protein() == null || macros.protein() >= 1.0)
        || (macros.omega3() == null || macros.omega3() >= 1.0)
        || (macros.omega6() == null || macros.omega6() >= 1.0)) {
      return Mono.error(
          new BadRequestException(
              "if one distributed macro is fill all must be filled with positive numbers less than 1"));
    }
    return Mono.just(macros);
  }
}

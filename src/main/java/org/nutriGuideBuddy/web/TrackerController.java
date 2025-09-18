package org.nutriGuideBuddy.web;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.shared.dto.MealConsumedView;
import org.nutriGuideBuddy.features.shared.dto.NutritionConsumedView;
import org.nutriGuideBuddy.features.tracker.dto.CalorieRequest;
import org.nutriGuideBuddy.features.tracker.dto.NutritionRequest;
import org.nutriGuideBuddy.features.tracker.dto.TrackerRequest;
import org.nutriGuideBuddy.features.tracker.dto.TrackerView;
import org.nutriGuideBuddy.features.tracker.service.TrackerService;
import org.nutriGuideBuddy.infrastructure.security.access_validator.UserAccessValidator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tracker/user/{userId}")
public class TrackerController {

  private final TrackerService service;
  private final UserAccessValidator userAccessValidator;

  @PostMapping
  public Mono<TrackerView> get(
      @RequestBody(required = false) @Valid TrackerRequest dto, @PathVariable Long userId) {
    return userAccessValidator.validateAccess(userId).then(service.get(dto, userId));
  }

  @PostMapping("/nutrition")
  public Mono<Map<LocalDate, Set<NutritionConsumedView>>> getNutritionAmountInRange(
      @RequestBody @Valid NutritionRequest request) {
    return service.getNutritionForRange(request);
  }

  @PostMapping("/calories")
  public Mono<Map<LocalDate, Set<MealConsumedView>>> getCaloriesInRange(
      @RequestBody(required = false) @Valid CalorieRequest request) {
    return service.getCaloriesInRange(request);
  }
}

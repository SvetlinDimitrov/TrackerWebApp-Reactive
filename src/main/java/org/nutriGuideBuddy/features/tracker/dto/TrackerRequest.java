package org.nutriGuideBuddy.features.tracker.dto;

import java.time.LocalDate;
import org.nutriGuideBuddy.features.tracker.annotaions.ValidGoal;

public record TrackerRequest(@ValidGoal String goal, LocalDate date) {
  public TrackerRequest {
    if (goal == null || goal.isBlank()) {
      goal = "MAINTENANCE";
    }
    if (date == null) {
      date = LocalDate.now();
    }
  }
}

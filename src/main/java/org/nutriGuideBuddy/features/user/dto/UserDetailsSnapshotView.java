package org.nutriGuideBuddy.features.user.dto;

import java.time.LocalDate;
import org.nutriGuideBuddy.features.tracker.enums.Goals;
import org.nutriGuideBuddy.features.user.enums.DuetTypes;
import org.nutriGuideBuddy.features.user.enums.Gender;
import org.nutriGuideBuddy.features.user.enums.WorkoutState;

public record UserDetailsSnapshotView(
    Long userId,
    Double kilograms,
    Double height,
    Integer age,
    WorkoutState workoutState,
    Gender gender,
    Goals goal,
    DuetTypes duet,
    LocalDate createdAt,
    LocalDate updatedAt) {}

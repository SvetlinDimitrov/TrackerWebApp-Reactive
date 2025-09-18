package org.nutriGuideBuddy.features.tracker.dto;

import java.time.LocalDate;

public record CalorieRequest(LocalDate startDate, LocalDate endDate) {}

package org.nutriGuideBuddy.features.user.dto;

import java.time.LocalDate;
import java.util.Set;

public record UserDetailsSnapshotFilter(
    LocalDate to,
    LocalDate from,
    Set<Long> idsIn,
    Set<Long> idsNotIn,
    CustomPageableUserDetailsSnapshot pageable) {}

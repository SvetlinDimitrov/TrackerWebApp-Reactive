package org.nutriGuideBuddy.features.user.dto;

import java.time.LocalDate;
import org.nutriGuideBuddy.features.user.enums.UserRole;

public record UserView(
    String id,
    String username,
    String email,
    LocalDate createdAt,
    LocalDate updatedAt,
    UserRole role) {}

package org.nutriGuideBuddy.features.user.dto;

import org.nutriGuideBuddy.features.user.enums.UserRole;

public record UserView(String id, String username, String email, UserRole role) {}

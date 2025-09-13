package org.nutriGuideBuddy.features.user.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 1, max = 255, message = "must be between 1 and 255 characters.") String username) {}

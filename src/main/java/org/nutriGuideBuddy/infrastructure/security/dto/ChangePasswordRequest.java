package org.nutriGuideBuddy.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "must not be blank")
        @Size(min = 4, max = 255, message = "must be between 4 and 255 characters")
        String newPassword) {}

package org.nutriGuideBuddy.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank(message = "must not be blank") @Email(message = "format is invalid") String email) {}

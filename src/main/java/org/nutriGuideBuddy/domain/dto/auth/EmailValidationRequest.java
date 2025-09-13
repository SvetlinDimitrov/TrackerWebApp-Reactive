package org.nutriGuideBuddy.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailValidationRequest(
    @NotBlank(message = "Must be present") @Email(message = "Must be a valid email")
        String email) {}

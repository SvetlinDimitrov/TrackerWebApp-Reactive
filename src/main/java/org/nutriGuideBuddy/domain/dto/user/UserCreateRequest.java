package org.nutriGuideBuddy.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "must not be blank.")
        @Size(min = 1, max = 255, message = "must be between 1 and 255 characters.")
        String username,
    @NotBlank(message = "must not be blank.")
        @Size(min = 4, max = 255, message = "must be between 4 and 255 characters.")
        String password) {}

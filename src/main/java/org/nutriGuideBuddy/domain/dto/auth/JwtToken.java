package org.nutriGuideBuddy.domain.dto.auth;

import java.time.LocalDateTime;

public record JwtToken(String value, LocalDateTime expiresIn) {}

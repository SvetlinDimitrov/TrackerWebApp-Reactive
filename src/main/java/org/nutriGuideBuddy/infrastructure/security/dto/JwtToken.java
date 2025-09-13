package org.nutriGuideBuddy.infrastructure.security.dto;

import java.time.LocalDateTime;

public record JwtToken(String value, LocalDateTime expiresIn) {}

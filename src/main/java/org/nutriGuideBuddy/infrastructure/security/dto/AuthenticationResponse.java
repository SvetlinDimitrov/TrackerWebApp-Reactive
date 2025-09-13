package org.nutriGuideBuddy.infrastructure.security.dto;

import org.nutriGuideBuddy.features.user.dto.UserWithDetailsView;

public record AuthenticationResponse(UserWithDetailsView userView, JwtToken accessToken) {}

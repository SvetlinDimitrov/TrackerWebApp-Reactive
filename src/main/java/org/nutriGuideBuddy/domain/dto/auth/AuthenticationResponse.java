package org.nutriGuideBuddy.domain.dto.auth;

import org.nutriGuideBuddy.domain.dto.user.UserWithDetailsView;

public record AuthenticationResponse(UserWithDetailsView userView, JwtToken accessToken) {}

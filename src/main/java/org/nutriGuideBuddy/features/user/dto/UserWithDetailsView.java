package org.nutriGuideBuddy.features.user.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record UserWithDetailsView(@JsonUnwrapped UserView user, UserDetailsView details) {}

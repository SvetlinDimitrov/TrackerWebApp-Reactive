package org.nutriGuideBuddy.features.user.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;

public record UserWithDetailsView(@JsonUnwrapped UserView user, UserDetailsView details) {}
